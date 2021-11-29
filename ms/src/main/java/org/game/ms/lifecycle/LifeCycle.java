/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.lifecycle;

import cn.hutool.json.JSONUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.map.WorldMap;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class LifeCycle {

    @Autowired
    private PlayerService playerService;
    @Autowired
    private WorldMap worldMap;
    private final Map<Long, Player> onlinePlayers = new HashMap<>();

    public Player createPlayer(String name) {
        //init player 
        Player player = playerService.createPlayer(name);

        //go to the world map
        player.setMap(worldMap);
        worldMap.playerComeInMap(player);

        //player online
        onlinePlayers.put(player.getId(), player);
        log.debug("player online {} ", JSONUtil.toJsonStr(player));
        return player;
    }

}
