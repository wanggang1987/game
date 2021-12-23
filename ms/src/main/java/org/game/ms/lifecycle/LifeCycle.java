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
import org.game.ms.client.ClientService;
import org.game.ms.fight.BattleService;
import org.game.ms.func.FuncUtils;
import org.game.ms.map.RootMap;
import org.game.ms.monster.Monster;
import org.game.ms.monster.MonsterService;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
import org.game.ms.reward.Experience;
import org.game.ms.reward.Gold;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.Role;
import org.game.ms.role.RoleType;
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
    private BattleService battleService;
    @Autowired
    private ClientService clientService;
    
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
    
    public Role getRole(RoleType type, Long id) {
        if (FuncUtils.equals(type, RoleType.MONSTER)) {
            return onlineMonster(id);
        }
        if (FuncUtils.equals(type, RoleType.PLAYER)) {
            return onlinePlayer(id);
        }
        return null;
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
    
    private void monsterDie() {
        List<Monster> deadList = onlineMonsters.values().stream()
                .filter(monster -> monster.getHealthPoint() < 1)
                .collect(Collectors.toList());
        deadList.forEach(monster -> {
            monster.setLivingStatus(LivingStatus.DEAD);
            monsterService.removeFromMap(monster);
            onlineMonsters.remove(monster.getId());
            monsterReward(monster);
            battleService.removeRoleFromBattle(monster);
            clientService.addRoleDieMsg(monster);
            log.debug("monster {} die", monster.getId());
        });
    }
    
    private void monsterReward(Monster monster) {
        int exp = Experience.MonsterExp(monster.getLevel());
        int coin = Gold.MonsterCoin(monster.getLevel());
        monster.getBattle().getPlayers().forEach(id -> {
            Player player = onlinePlayer(id);
            playerService.playerGetExp(player, exp);
            playerService.playerGetCoin(player, coin);
        });
    }
    
    public void lifeEnd() {
        monsterDie();
        playerDie();
        battleService.removeEndBattle();
    }
    
    private void playerDie() {
        List<Player> deadList = onlinePlayers.values().stream()
                .filter(player -> player.getHealthPoint() < 1)
                .collect(Collectors.toList());
        deadList.forEach(player -> {
            player.setLivingStatus(LivingStatus.DEAD);
            battleService.removeRoleFromBattle(player);
            log.debug("player {} die", player.getId());
        });
    }
    
}
