/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.Role;
import org.game.ms.role.RoleType;
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

    final protected int gridSize = 20;
    protected final List<Long> inMapPlayerIdList = new ArrayList<>();
    protected final Map<String, List<Long>> gridMonsterIdsMap = new ConcurrentHashMap<>();

    protected void addMonsterToMap(Role monster, Location location) {
        location.setGrid(locationInGrid(location));
        monster.setMap(this);
        monster.setLocation(location);
        addMonsterToGrid(monster);
    }

    private void addMonsterToGrid(Role monster) {
        List<Long> gridMonsterIds = gridMonsterIdsMap.get(monster.getLocation().getGrid());
        if (gridMonsterIds == null) {
            gridMonsterIds = new ArrayList<>();
            gridMonsterIdsMap.put(monster.getLocation().getGrid(), gridMonsterIds);
        }
        gridMonsterIds.add(monster.getId());
    }

    public void playerComeInMap(Player player) {
        inMapPlayerIdList.add(player.getId());
    }

    public void playerLeaveMap(Player player) {
        inMapPlayerIdList.remove(player.getId());
        player.setLocation(null);
    }

    private void removeMonsterFromGrid(Role monster) {
        List<Long> gridMonsterIds = gridMonsterIdsMap.get(monster.getLocation().getGrid());
        gridMonsterIds.remove(monster.getId());
    }

    public void removeMonsterFromMap(Role monster) {
        removeMonsterFromGrid(monster);
        log.debug("removeMonsterFromMap monster {} ", monster.getId());
    }

    protected void removeMonsters(List<Monster> monsters) {
        monsters.forEach(monster -> removeMonsterFromMap(monster));
    }

    private String gridStr(double lx, double ly, double lz) {
        int x = (int) (lx / gridSize);
        x = lx > 0 ? x + 1 : x - 1;
        int y = (int) (ly / gridSize);
        y = ly > 0 ? y + 1 : y - 1;
        return new StringBuilder().append("x:").append(x).append("y:").append(y).append("z:0").toString();
    }

    protected String locationInGrid(Location location) {
        return gridStr(location.getX(), location.getY(), location.getZ());
    }

    protected List<String> nearByGrids(Location location) {
        List<String> grids = new ArrayList<>();
        grids.add(gridStr(location.getX(), location.getY(), location.getZ()));
        grids.add(gridStr(location.getX() + 20, location.getY(), location.getZ()));
        grids.add(gridStr(location.getX(), location.getY() - 20, location.getZ()));
        grids.add(gridStr(location.getX() - 20, location.getY(), location.getZ()));
        grids.add(gridStr(location.getX(), location.getY() + 20, location.getZ()));
        grids.add(gridStr(location.getX() + 20, location.getY() - 20, location.getZ()));
        grids.add(gridStr(location.getX() - 20, location.getY() - 20, location.getZ()));
        grids.add(gridStr(location.getX() - 20, location.getY() + 20, location.getZ()));
        grids.add(gridStr(location.getX() + 20, location.getY() + 20, location.getZ()));
        return grids;
    }

    public List<Long> findNearByMonsterIdsForPlayer(Player player) {
        List<Long> monsterIds = new ArrayList<>();
        List<String> grids = nearByGrids(player.getLocation());
        for (String grid : grids) {
            List<Long> gridMonsters = gridMonsterIdsMap.get(grid);
            if (FuncUtils.notEmpty(gridMonsters)) {
                monsterIds.addAll(gridMonsters);
            }
        }
        return monsterIds;
    }

    public Long findNearByMonsterIdForPlayer(Player player) {
        List<String> grids = nearByGrids(player.getLocation());
        for (String grid : grids) {
            List<Long> gridMonsterIds = gridMonsterIdsMap.get(grid);
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
        double preDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        double moveDistance = role.getSpeed() * wheelConfig.getTickDuration();
        if (moveDistance > preDistance) {
            role.setMoveStatus(MoveStatus.STANDING);
            moveRoleToLocation(role, target.getLocation().getX(), target.getLocation().getY());
        } else {
            double x = role.getLocation().getX() + (xDistance / preDistance) * moveDistance;
            double y = role.getLocation().getY() + (yDistance / preDistance) * moveDistance;
            role.setMoveStatus(MoveStatus.MOVEING);
            moveRoleToLocation(role, x, y);
        }
    }

    private void moveRoleToLocation(Role role, double x, double y) {
        Location location = new Location(x, y, 0);
        location.setGrid(locationInGrid(location));
        if (FuncUtils.equals(role.getLocation().getGrid(), location.getGrid())) {
            role.setLocation(location);
        } else {
            if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
                removeMonsterFromGrid(role);
                role.setLocation(location);
                addMonsterToGrid(role);
            }
        }
    }
}
