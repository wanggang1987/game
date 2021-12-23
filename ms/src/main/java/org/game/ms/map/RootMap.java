/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.ClientService;
import org.game.ms.func.FuncUtils;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.Role;
import org.game.ms.timeline.WheelConfig;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author wanggang
 */
@Slf4j
public class RootMap {

    @Autowired
    private WheelConfig wheelConfig;
    @Autowired
    private ClientService clientService;
    @Autowired
    private GridService gridService;

    protected final List<Long> inMapPlayerIdList = new ArrayList<>();

    public void addPlayerToMap(Player player) {
        inMapPlayerIdList.add(player.getId());
        gridService.addRoleToGrid(player);

    }

    public void playerLeaveMap(Player player) {
        inMapPlayerIdList.remove(player.getId());
        gridService.removeRoleFromGrid(player);
        player.setLocation(null);
    }

    protected void addMonsterToMap(Role monster, Location location) {
        monster.setMap(this);
        monster.setLocation(location);
        gridService.addRoleToGrid(monster);
    }

    public void removeMonsterFromMap(Role monster) {
        gridService.removeRoleFromGrid(monster);
        log.debug("removeMonsterFromMap monster {} ", monster.getId());
    }

    protected void removeMonsters(List<Monster> monsters) {
        monsters.forEach(monster -> removeMonsterFromMap(monster));
    }

    public int findNearByMonsterNumForPlayer(Player player) {
        int n = 0;
        for (String grid : player.getLocation().getNearGrids()) {
            List<Long> gridMonsters = gridService.monsterIdsInGrid(grid);
            if (FuncUtils.notEmpty(gridMonsters)) {
                n += gridMonsters.size();
            }
        }
        return n;
    }

    public Long findNearByMonsterIdForPlayer(Player player) {
        for (String grid : player.getLocation().getNearGrids()) {
            List<Long> gridMonsterIds = gridService.monsterIdsInGrid(grid);
            if (FuncUtils.notEmpty(gridMonsterIds)) {
                int index = FuncUtils.randomZeroToRange(gridMonsterIds.size());
                Long id = gridMonsterIds.get(index);
                log.debug("findNearByMonsterForPlayer player {}  monster:{}", player.getId(), id);
                return id;
            }
        }
        return null;
    }

    public void roleMoveToTargetInTick(Role role, Role target) {
        double xDistance = target.getLocation().getX() - role.getLocation().getX();
        double yDistance = target.getLocation().getY() - role.getLocation().getY();
        double targetDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        double moveDistance = role.getSpeed() * wheelConfig.getTickDuration();
        if (moveDistance > targetDistance) {
            role.setMoveStatus(MoveStatus.STANDING);
            moveRoleToLocation(role, target.getLocation().getX(), target.getLocation().getY(), 0);
        } else {
            double x = role.getLocation().getX() + (xDistance / targetDistance) * moveDistance;
            double y = role.getLocation().getY() + (yDistance / targetDistance) * moveDistance;
            role.setMoveStatus(MoveStatus.MOVEING);
            moveRoleToLocation(role, x, y, 0);
        }
    }

    private void moveRoleToLocation(Role role, double x, double y, double z) {
        if (FuncUtils.equals(role.getLocation().getGrid(), gridService.gridStr(x, y, z))) {
            role.getLocation().setX(x);
            role.getLocation().setY(y);
            role.getLocation().setZ(z);
            clientService.addRoleMoveMsg(role);
        } else {
            gridService.removeRoleFromGrid(role);
            Location location = new Location(x, y, z);
            gridService.locationGrids(location);
            log.debug("{} {} change grid from {} to {}",
                    role.getRoleType(), role.getId(), role.getLocation().getGrid(), location.getGrid());
            role.setLocation(location);
            gridService.addRoleToGrid(role);
        }
    }
}
