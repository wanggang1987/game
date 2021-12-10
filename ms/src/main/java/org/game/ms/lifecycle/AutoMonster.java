/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.fight.FightService;
import org.game.ms.func.FuncUtils;
import org.game.ms.monster.Monster;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.RoleService;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class AutoMonster {

    @Autowired
    private FightService fightService;
    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private RoleService roleService;

    private void MonsterAuto(Monster monster) {
        if (monster.getBattle() == null) {
            monster.setTargetId(null);
            return;
        }
        if (monster.getBattle() != null && monster.getTargetId() == null) {
            int index = FuncUtils.randomZeroToRange(monster.getBattle().getPlayers().size());
            monster.setTargetType(RoleType.PLAYER);
            monster.setTargetId(monster.getBattle().getPlayers().get(index));
            return;
        }
        if (monster.getTargetId() == null) {
            return;
        }
        autoAttack(monster);
        autoMove(monster);
    }

    private void autoAttack(Monster monster) {
        fightService.fight(monster);
    }

    public void autoMonsterForTick() {
        lifeCycle.onlineMonsters().forEach(monster -> MonsterAuto(monster));
    }

    private void autoMove(Monster monster) {
        if (FuncUtils.equals(monster.getAttackStatus(), AttackStatus.OUT_RANGE)
                && FuncUtils.equals(monster.getMoveStatus(), MoveStatus.STANDING)) {
            log.debug("Monster {} start move to {} {}", monster.getId(), monster.getTargetType(), monster.getTargetId());
            monster.setMoveStatus(MoveStatus.MOVEING);
        }
        if (FuncUtils.notEquals(monster.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            monster.setMoveStatus(MoveStatus.STANDING);
        }
        if (FuncUtils.equals(monster.getMoveStatus(), MoveStatus.MOVEING)) {
            roleService.moveToTargetInTick(monster);
        }
    }
}
