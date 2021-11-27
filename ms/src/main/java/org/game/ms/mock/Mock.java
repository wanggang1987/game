/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.mock;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostConstruct
    private void init() {
        Player player = playerService.createPlayer("战士");
        
    }
}
