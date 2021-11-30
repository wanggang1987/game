/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.Role;
import org.game.ms.timeline.TimeWheel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author wanggang
 */
@Slf4j
public class RootMap {

    @Autowired
    private TimeWheel timeWheel;
    @Autowired
    private LifeCycle licCycle;

    final protected int gridSize = 20;
    protected final List<Long> inMapPlayerIdList = new ArrayList<>();
    protected final Map<String, List<Long>> gridMonsterIdsMap = new HashMap<>();

    protected void addMonsterToMap(Monster monster, Location location) {
        location.setGrid(locationInGrid(location));
        monster.setMap(this);
        monster.setLocation(location);
        List<Long> gridMonsterIds = gridMonsterIdsMap.get(monster.getLocation().getGrid());
        if (gridMonsterIds == null) {
            gridMonsterIds = new ArrayList<>();
            gridMonsterIdsMap.put(monster.getLocation().getGrid(), gridMonsterIds);
        }
        gridMonsterIds.add(monster.getId());
    }

    protected void playerComeInMap(Player player) {
        inMapPlayerIdList.add(player.getId());
    }

    public void removeMonsterFromMap(Role monster) {
        List<Long> gridMonsterIds = gridMonsterIdsMap.get(monster.getLocation().getGrid());
        gridMonsterIds.remove(monster.getId());
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
            if (CollectionUtil.isNotEmpty(gridMonsters)) {
                monsterIds.addAll(gridMonsters);
            }
        }
        log.debug("findNearByMonsterIdsForPlayer player {}  monsters:{}", player.getId(), monsterIds.size());
        return monsterIds;
    }

    public Long findNearByMonsterIdForPlayer(Player player) {
        List<String> grids = nearByGrids(player.getLocation());
        for (String grid : grids) {
            List<Long> gridMonsterIds = gridMonsterIdsMap.get(grid);
            if (CollectionUtil.isNotEmpty(gridMonsterIds)) {
                Long id = gridMonsterIds.stream().findAny().orElse(null);
                log.debug("findNearByMonsterForPlayer player {}  monster:{}", player.getId(), id);
                return id;
            }
        }
        return null;
    }

    public void playerMoveToTargetInTick(Player player) {
        double xDistance = player.getTarget().getLocation().getX() - player.getLocation().getX();
        double yDistance = player.getTarget().getLocation().getY() - player.getLocation().getY();
        double preDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        double moveDistance = player.getSpeed() / timeWheel.getTicksPerWheel();
        if (moveDistance > preDistance) {
            player.setMoveStatus(MoveStatus.STANDING);
            movePlayerToLocation(player, player.getTarget().getLocation());
        } else {
            double x = player.getLocation().getX() + (xDistance / preDistance) * moveDistance;
            double y = player.getLocation().getY() + (yDistance / preDistance) * moveDistance;
            Location location = new Location(x, y, 0);
            location.setGrid(locationInGrid(location));
            player.setMoveStatus(MoveStatus.MOVEING);
            movePlayerToLocation(player, location);
        }
    }

    private void movePlayerToLocation(Player player, Location location) {
        player.setLocation(location);
    }
}
