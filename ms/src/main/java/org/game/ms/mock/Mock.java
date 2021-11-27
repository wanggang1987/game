/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.mock;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.map.Location;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
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

    @Autowired
    private PlayerService playerService;
    private Player player;

    @PostConstruct
    private void init() throws InterruptedException {
        player = playerService.createPlayer("战士");

    }

    @Scheduled(fixedRate = 1000 * 6)
    private void move() {
        player.setLocation(new Location(FuncUtils.randomInRange(0, 100), FuncUtils.randomInRange(0, 100), 0));
        log.debug("move to {}", player.getLocation());
    }
}
