/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
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

    final protected int gridSize = 20;
    protected final List<Player> inMapPlayerList = new ArrayList<>();
    protected final Map<Long, List<Monster>> playerMonsterMap = new HashMap<>();
    protected final Map<String, List<Monster>> gridMonsterMap = new HashMap<>();

    protected void playerComeInMap(Player player) {
        List<Monster> monsters = playerMonsterMap.get(player.getId());
        if (monsters == null) {
            playerMonsterMap.put(player.getId(), new ArrayList<>());
        }
        inMapPlayerList.add(player);
    }

    protected void insertMonsterToGrid(Monster monster) {
        List<Monster> gridMonsters = gridMonsterMap.get(monster.getLocation().getGrid());
        if (gridMonsters == null) {
            gridMonsters = new ArrayList<>();
            gridMonsterMap.put(monster.getLocation().getGrid(), gridMonsters);
        }
        gridMonsters.add(monster);
    }

    protected void destoryMonsters(List<Monster> monsters) {
        monsters.forEach(monster -> {
            List<Monster> gridMonsters = gridMonsterMap.get(monster.getLocation().getGrid());
            gridMonsters.remove(monster);
        });
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

    public Role findNearByMonsterForPlayer(Player player) {
        Monster monster = null;
        List<String> grids = nearByGrids(player.getLocation());
        for (String grid : grids) {
            List<Monster> gridMonsters = gridMonsterMap.get(grid);
            if (CollectionUtil.isNotEmpty(gridMonsters)) {
                monster = gridMonsters.stream().findAny().orElse(null);
                break;
            }
        }
        if (monster == null) {
            return null;
        }
        log.info("findNearByMonsterForPlayer player {}  monster {}", player.getId(), JSONUtil.toJsonStr(monster));
        return monster;
    }

    public void playerMoveToTargetInTick(Player player) {
        double xDistance = player.getTarget().getLocation().getX() - player.getLocation().getX();
        double yDistance = player.getTarget().getLocation().getY() - player.getLocation().getY();
        double preDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        double moveDistance = player.getSpeed() / timeWheel.getTicksPerWheel();
        if (moveDistance > preDistance) {
            movePlayerToLocation(player, player.getTarget().getLocation());
        } else {
            double x = player.getLocation().getX() + (xDistance / preDistance) * moveDistance;
            double y = player.getLocation().getY() + (yDistance / preDistance) * moveDistance;
            Location location = new Location(x, y, 0);
            location.setGrid(locationInGrid(location));
            movePlayerToLocation(player, location);
        }
    }

    private void movePlayerToLocation(Player player, Location location) {
        player.setLocation(location);
    }
}
