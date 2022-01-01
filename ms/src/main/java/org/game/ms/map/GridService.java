/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.game.ms.client.MessageService;
import org.game.ms.func.FuncUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
import org.game.ms.role.Role;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.JsonUtils;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class GridService {

    @Autowired
    private MessageService messageService;
    @Autowired
    private LifeCycle lifeCycle;

    private final int gridSize = 20;
    private final Map<String, Grid> gridMap = new HashMap<>();

    public int gridSize() {
        return gridSize;
    }

    public String gridStr(double lx, double ly, double lz) {
        int x = (int) (lx / gridSize);
        x = lx > 0 ? x + 1 : x - 1;
        int y = (int) (ly / gridSize);
        y = ly > 0 ? y + 1 : y - 1;
        return new StringBuilder().append("x:").append(x).append("y:").append(y).append("z:0").toString();
    }

    public void locationGrids(Location location) {
        location.setGrid(gridStr(location.getX(), location.getY(), location.getZ()));
        location.setNearGrids(new ArrayList<>());
        location.getNearGrids().add(gridStr(location.getX(), location.getY(), location.getZ()));
        location.getNearGrids().add(gridStr(location.getX() + 20, location.getY(), location.getZ()));
        location.getNearGrids().add(gridStr(location.getX(), location.getY() - 20, location.getZ()));
        location.getNearGrids().add(gridStr(location.getX() - 20, location.getY(), location.getZ()));
        location.getNearGrids().add(gridStr(location.getX(), location.getY() + 20, location.getZ()));
        location.getNearGrids().add(gridStr(location.getX() + 20, location.getY() - 20, location.getZ()));
        location.getNearGrids().add(gridStr(location.getX() - 20, location.getY() - 20, location.getZ()));
        location.getNearGrids().add(gridStr(location.getX() - 20, location.getY() + 20, location.getZ()));
        location.getNearGrids().add(gridStr(location.getX() + 20, location.getY() + 20, location.getZ()));
    }

    private Grid getGrid(String gridStr) {
        Grid grid = gridMap.get(gridStr);
        if (FuncUtils.isEmpty(grid)) {
            grid = new Grid();
            grid.setGrid(gridStr);
            gridMap.put(gridStr, grid);
        }
        return grid;
    }

    public void addRoleToGrid(Role role) {
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            Player player = lifeCycle.onlinePlayer(role.getId());
            addPlayerToGrid(player);
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            Monster monster = lifeCycle.onlineMonster(role.getId());
            addMonsterToGrid(monster);
        }
    }

    public void addPlayerToGrid(Player player) {
        playersInGrid(player.getLocation().getGrid()).add(player);
        messageService.addRoleToGridMsg(player);
    }

    public void addMonsterToGrid(Monster monster) {
        monstersInGrid(monster.getLocation().getGrid()).add(monster);
        messageService.addRoleToGridMsg(monster);
    }

    public void removeRoleFromGrid(Role role) {
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            Player player = lifeCycle.onlinePlayer(role.getId());
            removePlayerFromGrid(player);
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            Monster monster = lifeCycle.onlineMonster(role.getId());
            removeMonsterFromGrid(monster);
        }
    }

    public void removePlayerFromGrid(Player player) {
        List<Player> players = playersInGrid(player.getLocation().getGrid());
        Iterator it = players.iterator();
        while (it.hasNext()) {
            Player next = (Player) it.next();
            if (FuncUtils.equals(next.getId(), player.getId())) {
                it.remove();
                break;
            }
        }
    }

    public void removeMonsterFromGrid(Monster monster) {
        List<Monster> monsters = monstersInGrid(monster.getLocation().getGrid());
        Iterator it = monsters.iterator();
        while (it.hasNext()) {
            Monster next = (Monster) it.next();
            if (FuncUtils.equals(next.getId(), monster.getId())) {
                it.remove();
                break;
            }
        }
    }

    public List<Monster> monstersInGrid(String grid) {
        return getGrid(grid).getMonsters();
    }

    public List<Monster> monstersInNearGrid(Location location) {
        List<Monster> monsters = new ArrayList<>();
        location.getNearGrids().forEach(grid -> {
            monsters.addAll(getGrid(grid).getMonsters());
        });
        return monsters;
    }

    public List<Player> playersInGrid(String grid) {
        return getGrid(grid).getPlayers();
    }

    public List<Player> playersInNearGrids(Location location) {
        List<Player> players = new ArrayList<>();
        location.getNearGrids().forEach(grid -> {
            players.addAll(getGrid(grid).getPlayers());
        });
        return players;
    }
}
