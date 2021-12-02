/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.mock;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.lifecycle.AutoPlayer;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.map.WorldMap;
import org.game.ms.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Slf4j
@Component
public class Mock {

    private Player player;

    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private AutoPlayer autoPlay;
    @Autowired
    private WorldMap worldMap;

    @PostConstruct
    private void init() throws InterruptedException {
        player = lifeCycle.createPlayer("战士");

        //go to the world map
        player.setMap(worldMap);
        worldMap.playerComeInMap(player);
        autoPlay.startPlayerAutoPlay(player);
    }

//    @Scheduled(fixedRate = 1000)
//    private void showPlayer() {
//        log.debug("{}", JSONUtil.toJsonStr(player));
//    }
}
