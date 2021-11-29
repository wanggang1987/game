/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.mock;

import cn.hutool.json.JSONUtil;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.lifecycle.AutoPlay;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    private AutoPlay autoPlay;
    
    @PostConstruct
    private void init() throws InterruptedException {
        player = lifeCycle.createPlayer("战士");
        autoPlay.startPlayerAutoPlay(player);
    }
    
    @Scheduled(fixedRate = 1000)
    private void showPlayer() {
        log.debug("{}", JSONUtil.toJsonStr(player));
    }
}
