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
import org.game.ms.map.RootMap;
import org.game.ms.monster.Monster;
import org.game.ms.monster.MonsterService;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
import org.game.ms.role.Role;
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
    private MonsterService monsterService;

    private final Map<Long, RootMap> maps = new HashMap<>();
    private final Map<Long, Player> onlinePlayers = new HashMap<>();
    private final Map<Long, Monster> onlineMonsters = new HashMap<>();

    public Player createPlayer(String name) {
        //init player 
        Player player = playerService.createPlayer(name);
        //player online
        onlinePlayers.put(player.getId(), player);
        log.debug("player online {} ", JSONUtil.toJsonStr(player));
        return player;
    }

    public Player onlinePlayer(Long id) {
        return onlinePlayers.get(id);
    }

    public Monster onlineMonster(Long id) {
        return onlineMonsters.get(id);
    }

    public Monster createMonster() {
        Monster monster = monsterService.initMonster();
        onlineMonsters.put(monster.getId(), monster);
        log.debug("createMonster {}", JSONUtil.toJsonStr(monster));
        return monster;
    }

    public void detoryMonster(Role monster) {
        monster.getMap().removeMonsterFromMap(monster);
        onlineMonsters.remove(monster.getId());
        log.debug("detoryMonster {}", monster.getId());
    }
}
