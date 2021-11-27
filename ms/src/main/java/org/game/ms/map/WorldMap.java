/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import org.game.ms.func.FuncUtils;
import org.game.ms.player.Player;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Component
public class WorldMap extends GameMap {

    private String name = "艾泽拉斯";
    final protected int randomRange = 200;

    @Override
    public Location playerComeInMap(Player player) {
        players.add(player);
        return new Location(FuncUtils.randomInRange(0, randomRange), FuncUtils.randomInRange(0, randomRange), 0);
    }
}
