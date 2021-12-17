/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.id.IdService;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
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
        List<Battle> removeList = battles.stream()
                .filter(battle -> battle.getPlayers().isEmpty() || battle.getMonsters().isEmpty())
                .collect(Collectors.toList());
        removeList.forEach(battle -> {
            battle.getPlayers().forEach(id -> lifeCycle.onlinePlayer(id).setBattle(null));
            battle.getMonsters().forEach(id -> lifeCycle.onlineMonster(id).setBattle(null));
            log.debug("battle end {}", battle);
        });
        battles.removeAll(removeList);
    }

    private void addRoleToBattle(Role role, Battle battle) {
        role.setBattle(battle);
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            battle.getPlayers().add(role.getId());
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            battle.getMonsters().add(role.getId());
        }
    }

    public void removeRoleFromBattle(Role role) {
        Battle battle = role.getBattle();
        if (battle == null) {
            return;
        }
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            battle.getPlayers().remove(role.getId());
            role.setBattle(null);
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            battle.getMonsters().remove(role.getId());
            role.setBattle(null);
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
            targetBattle.getPlayers().forEach(id -> {
                Player player = lifeCycle.onlinePlayer(id);
                player.setBattle(newBattle);
            });
            newBattle.getMonsters().addAll(targetBattle.getMonsters());
            targetBattle.getMonsters().forEach(id -> {
                Monster monster = lifeCycle.onlineMonster(id);
                monster.setBattle(newBattle);
            });
        }
        log.debug("battle {} players {} monsters {}", newBattle.getId(), newBattle.getPlayers().size(), newBattle.getMonsters().size());
    }
}
