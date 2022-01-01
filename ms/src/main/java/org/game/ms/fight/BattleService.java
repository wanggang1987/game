/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.id.IdService;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
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
public class BattleService {

    @Autowired
    private IdService idService;
    @Autowired
    private LifeCycle lifeCycle;
    private final List<Battle> battles = new ArrayList<>();

    private Battle createNewBattle() {
        Battle battle = new Battle();
        battle.setId(idService.newId());
        battles.add(battle);
        return battle;
    }

    public void removeEndBattle() {
        Iterator it = battles.iterator();
        while (it.hasNext()) {
            Battle battle = (Battle) it.next();
            if (battle.getPlayers().isEmpty() || battle.getMonsters().isEmpty()) {
                battle.getPlayers().forEach(player -> player.setBattle(null));
                battle.getMonsters().forEach(monster -> monster.setBattle(null));
                it.remove();
                log.debug("battle end {}", battle);
            }
        }
    }

    private void addRoleToBattle(Role role, Battle battle) {
        if (FuncUtils.notEquals(role.getLivingStatus(), LivingStatus.LIVING)) {
            return;
        }
        role.setBattle(battle);
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            Player player = lifeCycle.onlinePlayer(role.getId());
            battle.getPlayers().add(player);
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            Monster monster = lifeCycle.onlineMonster(role.getId());
            battle.getMonsters().add(monster);
        }
    }

    public void removeRoleFromBattle(Role role) {
        Battle battle = role.getBattle();
        if (battle == null) {
            return;
        }
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            Player player = lifeCycle.onlinePlayer(role.getId());
            Iterator it = battle.getPlayers().iterator();
            while (it.hasNext()) {
                Player next = (Player) it.next();
                if (FuncUtils.equals(next.getId(), player.getId())) {
                    it.remove();
                    player.setBattle(null);
                    break;
                }
            }
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            Monster monster = lifeCycle.onlineMonster(role.getId());
            Iterator it = battle.getMonsters().iterator();
            while (it.hasNext()) {
                Monster next = (Monster) it.next();
                if (FuncUtils.equals(next.getId(), monster.getId())) {
                    it.remove();
                    monster.setBattle(null);
                    break;
                }
            }
        }
        log.debug("battle {} players {} monsters {}", battle.getId(), battle.getPlayers().size(), battle.getMonsters().size());
    }

    public void addFightStatus(Role source, Role target) {
        Battle sourceBattle = source.getBattle();
        Battle targetBattle = target.getBattle();
        Battle newBattle;
        if (sourceBattle == null && targetBattle == null) {
            newBattle = createNewBattle();
            addRoleToBattle(source, newBattle);
            addRoleToBattle(target, newBattle);
        } else if (sourceBattle == null) {
            newBattle = targetBattle;
            addRoleToBattle(source, newBattle);
        } else if (targetBattle == null) {
            newBattle = sourceBattle;
            addRoleToBattle(target, newBattle);
        } else if (sourceBattle.equals(targetBattle)) {
            return;
        } else {
            newBattle = sourceBattle;
            newBattle.getPlayers().addAll(targetBattle.getPlayers());
            targetBattle.getPlayers().forEach(player -> {
                player.setBattle(newBattle);
            });
            newBattle.getMonsters().addAll(targetBattle.getMonsters());
            targetBattle.getMonsters().forEach(monster -> {
                monster.setBattle(newBattle);
            });
        }
        log.debug("battle {} players {} monsters {}", newBattle.getId(), newBattle.getPlayers().size(), newBattle.getMonsters().size());
    }

    public Monster findMonsterFromBattle(Battle battle) {
        if (FuncUtils.isEmpty(battle)) {
            return null;
        }
        return battle.getMonsters().stream().findAny().orElse(null);
    }
}
