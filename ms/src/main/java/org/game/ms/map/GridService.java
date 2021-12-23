/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.ClientService;
import org.game.ms.func.FuncUtils;
import org.game.ms.role.Role;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class GridService {

    @Autowired
    private ClientService clientService;
    
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

    private List<Long> roleIdsInGrid(Role role) {
        return roleIdsInGrid(role.getRoleType(), role.getLocation().getGrid());
    }

    private List<Long> roleIdsInGrid(RoleType type, String gridStr) {
        Grid grid = gridMap.get(gridStr);
        if (FuncUtils.isEmpty(grid)) {
            grid = new Grid();
            grid.setGrid(gridStr);
            gridMap.put(gridStr, grid);
        }
        if (FuncUtils.equals(type, RoleType.MONSTER)) {
            return grid.getMonsterIds();
        } else if (FuncUtils.equals(type, RoleType.PLAYER)) {
            return grid.getPlayerIds();
        }
        return null;
    }

    public void addRoleToGrid(Role role) {
        roleIdsInGrid(role).add(role.getId());
        clientService.addRoleToGridMsg(role);
    }

    public void removeRoleFromGrid(Role role) {
        roleIdsInGrid(role).remove(role.getId());
    }

    public List<Long> monsterIdsInGrid(String grid) {
        return roleIdsInGrid(RoleType.MONSTER, grid);
    }

    public List<Long> playerIdsInGrid(String grid) {
        return roleIdsInGrid(RoleType.PLAYER, grid);
    }
}
