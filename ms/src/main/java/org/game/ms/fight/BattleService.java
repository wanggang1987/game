/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.id.IdService;
import org.game.ms.lifecycle.LifeCycle;
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

    private Battle createNewBattle() {
        Battle battle = new Battle();
        battle.setId(idService.newId());
        return battle;
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
            if (battle.getPlayers().isEmpty()) {
                battle.getMonsters().forEach(id -> lifeCycle.onlineMonster(id).setBattle(null));
            }
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            battle.getMonsters().remove(role.getId());
            role.setBattle(null);
            if (battle.getMonsters().isEmpty()) {
                battle.getPlayers().forEach(id -> lifeCycle.onlinePlayer(id).setBattle(null));
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
            newBattle.getMonsters().addAll(targetBattle.getMonsters());
        }
        log.debug("battle {} players {} monsters {}", newBattle.getId(), newBattle.getPlayers().size(), newBattle.getMonsters().size());
    }
}
