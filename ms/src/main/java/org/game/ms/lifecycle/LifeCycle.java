/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.lifecycle;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.fight.BattleService;
import org.game.ms.func.FuncUtils;
import org.game.ms.map.RootMap;
import org.game.ms.map.WorldMap;
import org.game.ms.monster.Monster;
import org.game.ms.monster.MonsterService;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
import org.game.ms.role.Experience;
import org.game.ms.role.LivingStatus;
import org.game.ms.timeline.WheelConfig;
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
    @Autowired
    private WheelConfig wheelConfig;
    @Autowired
    private BattleService battleService;

    private WorldMap worldMap;
    private final Map<Long, RootMap> maps = new ConcurrentHashMap<>();
    private final Map<Long, Player> onlinePlayers = new ConcurrentHashMap<>();
    private final Map<Long, Monster> onlineMonsters = new ConcurrentHashMap<>();

    public void playerOnline(Player player) {
        onlinePlayers.put(player.getId(), player);
    }

    public Player onlinePlayer(Long id) {
        if (id == null) {
            return null;
        }
        return onlinePlayers.get(id);
    }

    public Monster onlineMonster(Long id) {
        if (id == null) {
            return null;
        }
        return onlineMonsters.get(id);
    }

    public Collection<Player> onlinePlayers() {
        return onlinePlayers.values();
    }

    public Collection<Monster> onlineMonsters() {
        return onlineMonsters.values();
    }

    public Monster createMonsterByLevel(int level) {
        Monster monster = monsterService.initMonsterByLevel(level);
        onlineMonsters.put(monster.getId(), monster);
        log.debug("createMonster {}", monster.getId());
        return monster;
    }

    public void cooldownTimer() {
        onlinePlayers.values().stream()
                .filter(player -> FuncUtils.numberCompare(player.getAttackCooldown(), 0) == 1)
                .forEach(player -> {
                    player.setAttackCooldown(player.getAttackCooldown() - wheelConfig.getTickDuration());
                    if (FuncUtils.numberCompare(player.getAttackCooldown(), 0) == -1) {
                        player.setAttackCooldown(0);
                    }
                });
        onlineMonsters.values().stream()
                .filter(monster -> FuncUtils.numberCompare(monster.getAttackCooldown(), 0) == 1)
                .forEach(monster -> {
                    monster.setAttackCooldown(monster.getAttackCooldown() - wheelConfig.getTickDuration());
                    if (FuncUtils.numberCompare(monster.getAttackCooldown(), 0) == -1) {
                        monster.setAttackCooldown(0);
                    }
                });
    }

    public void monsterDie() {
        List<Monster> deadList = onlineMonsters.values().stream()
                .filter(monster -> FuncUtils.numberCompare(monster.getHealthPoint(), 0.5) == -1)
                .collect(Collectors.toList());
        deadList.forEach(monster -> {
            monster.setLivingStatus(LivingStatus.DEAD);
            monster.getMap().removeMonsterFromMap(monster);
            onlineMonsters.remove(monster.getId());
            battleReward(monster);
            log.debug("monster {} die", monster.getId());
        });
    }

    public void battleReward(Monster monster) {
        int exp = Experience.MonsterExp(monster.getLevel());
        monster.getBattle().getPlayers().forEach(id -> {
            Player player = onlinePlayer(id);
            playerService.playerGetExp(player, exp);
        });
        battleService.removeRoleFromBattle(monster);
    }
}
