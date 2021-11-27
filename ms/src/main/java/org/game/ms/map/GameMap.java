/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import java.util.ArrayList;
import java.util.List;
import org.game.ms.player.Player;

/**
 *
 * @author wanggang
 */
public class GameMap {

    protected List<Player> players = new ArrayList<>();

    protected Location playerComeInMap(Player player) {
        return new Location(0, 0, 0);
    }
}
