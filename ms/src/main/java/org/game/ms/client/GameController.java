/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.JsonUtils;
import org.game.ms.lifecycle.AutoPlayer;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.map.WorldMap;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping("client")
public class GameController {

    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private AutoPlayer autoPlay;
    @Autowired
    private WorldMap worldMap;
    @Autowired
    private PlayerService playerService;

    @PostMapping("createPlayer")
    public void createPlayer() {
        Player player = playerService.createPlayer("测试");
        lifeCycle.playerOnline(player);
        playerService.playerGotoMap(player, worldMap);
        autoPlay.startPlayerAutoPlay(player);
        log.debug("{}", JsonUtils.bean2json(player));
    }

    @GetMapping("player/{id}")
    private Player getPlayer(@PathVariable("id") long id) {
        return lifeCycle.onlinePlayer(id);
    }

    @GetMapping("monster/{id}")
    private Monster getMonster(@PathVariable("id") long id) {
        return lifeCycle.onlineMonster(id);
    }
}
