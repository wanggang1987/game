/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.MessageService;
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
    private MessageService messageService;
    @Autowired
    private GridService gridService;

    protected final List<Long> inMapPlayerIdList = new ArrayList<>();

    public void addPlayerToMap(Player player) {
        player.setMap(this);
        inMapPlayerIdList.add(player.getId());
        gridService.addPlayerToGrid(player);
    }

    public void playerLeaveMap(Player player) {
        inMapPlayerIdList.remove(player.getId());
        gridService.removePlayerFromGrid(player);
        player.setLocation(null);
    }

    protected void addMonsterToMap(Monster monster, Location location) {
        monster.setMap(this);
        monster.setLocation(location);
        gridService.addMonsterToGrid(monster);
    }

    public void removeMonsterFromMap(Monster monster) {
        gridService.removeMonsterFromGrid(monster);
        log.debug("removeMonsterFromMap monster {} ", monster.getId());
    }

    public int findNearByMonsterNumForPlayer(Player player) {
        int n = 0;
        for (String grid : player.getLocation().getNearGrids()) {
            n += gridService.monstersInGrid(grid).size();
        }
        return n;
    }

    public Monster findNearByMonsterIdForPlayer(Player player) {
        for (String grid : player.getLocation().getNearGrids()) {
            Monster monster = gridService.monstersInGrid(grid).stream().findAny().orElse(null);
            if (FuncUtils.notEmpty(monster)) {
                log.debug("findNearByMonsterForPlayer player {}  monster:{}", player.getId(), monster.getId());
                return monster;
            }
        }
        return null;
    }

    public void roleMoveToTargetInTick(Role role) {
        Role target = role.getTarget();
        double xDistance = target.getLocation().getX() - role.getLocation().getX();
        double yDistance = target.getLocation().getY() - role.getLocation().getY();
        double targetDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        double moveDistance = role.getSpeed() * wheelConfig.getTickDuration();
        if (moveDistance > targetDistance) {
            role.setMoveStatus(MoveStatus.STANDING);
            moveRoleToLocation(role, target.getLocation().getX(), target.getLocation().getY(), 0);
        } else {
            double x = role.getLocation().getX() + (moveDistance / targetDistance) * xDistance;
            double y = role.getLocation().getY() + (moveDistance / targetDistance) * yDistance;
            role.setMoveStatus(MoveStatus.MOVEING);
            moveRoleToLocation(role, x, y, 0);
        }
    }

    public void roleChargeToTargetInTick(Role role) {
        Role target = role.getTarget();
        double xDistance = target.getLocation().getX() - role.getLocation().getX();
        double yDistance = target.getLocation().getY() - role.getLocation().getY();
        double targetDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        double moveDistance = role.getSpeed() * wheelConfig.getTickDuration() * 10;
        if (moveDistance > targetDistance - 1) {
            role.setMoveStatus(MoveStatus.STANDING);
            double x = target.getLocation().getX() - (1 / targetDistance) * xDistance;
            double y = target.getLocation().getY() - (1 / targetDistance) * yDistance;
            moveRoleToLocation(role, x, y, 0);
        } else {
            role.setMoveStatus(MoveStatus.MOVEING);
            double x = role.getLocation().getX() + (moveDistance / targetDistance) * xDistance;
            double y = role.getLocation().getY() + (moveDistance / targetDistance) * yDistance;
            moveRoleToLocation(role, x, y, 0);
        }
    }

    private void moveRoleToLocation(Role role, double x, double y, double z) {
        if (FuncUtils.equals(role.getLocation().getGrid(), gridService.gridStr(x, y, z))) {
            role.getLocation().setX(x);
            role.getLocation().setY(y);
            role.getLocation().setZ(z);
            messageService.addRoleMoveMsg(role);
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
