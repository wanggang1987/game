/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import java.util.ArrayList;
import java.util.List;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;

/**
 *
 * @author wanggang
 */
public class RootMap {

    protected List<Player> players = new ArrayList<>();
    final protected int gridSize = 20;

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

        return null;
    }

    protected boolean isFarAway(Location playerLocation, Location monsterLocation) {
        double x = playerLocation.getX() - monsterLocation.getX();
        double y = playerLocation.getY() - monsterLocation.getY();
        return x * x + y * y >= gridSize * gridSize;
    }

    protected Location playerComeInMap(Player player) {
        Location location = new Location(0, 0, 0);
        location.setGrid(locationInGrid(location));
        return location;
    }

    protected void move(Player play, Location target) {

    }

    protected void flushAndRrmoveMonsterForPlayer() {
    }

    protected void destoryFarAyayMonster(Player player, List<Monster> monsters) {
    }

    protected void createMonsterAroundPlayer(Player player, List<Monster> monsters, int num) {

    }
}
