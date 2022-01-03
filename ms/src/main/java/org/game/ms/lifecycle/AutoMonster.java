/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.fight.BufferEffectService;
import org.game.ms.fight.FightService;
import org.game.ms.func.FuncUtils;
import org.game.ms.map.RootMap;
import org.game.ms.monster.Monster;
import org.game.ms.monster.MonsterService;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.MoveStatus;
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
    private MonsterService monsterService;
    @Autowired
    private RootMap rootMap;
    @Autowired
    private BufferEffectService bufferEffectService;

    private void MonsterAuto(Monster monster) {
        if (FuncUtils.isEmpty(monster.getBattle()) && FuncUtils.notEmpty(monster.getTarget())) {
            monsterService.initMonster(monster);
            monster.setTarget(null);
            return;
        }
        if (FuncUtils.notEmpty(monster.getBattle()) && FuncUtils.isEmpty(monster.getTarget())) {
            monster.setTarget(monster.getBattle().getPlayers().stream().findAny().orElse(null));
            return;
        }
        if (FuncUtils.isEmpty(monster.getTarget())) {
            return;
        }

        if (bufferEffectService.anomalyPass(monster)) {
            return;
        }

        autoAttack(monster);
        autoMove(monster);
    }

    private void autoAttack(Monster monster) {
        fightService.autoFight(monster);
    }

    public void autoMonsterForTick() {
        lifeCycle.onlineMonsters().forEach(monster -> MonsterAuto(monster));
    }

    private void autoMove(Monster monster) {
        if (FuncUtils.equals(monster.getAttackStatus(), AttackStatus.OUT_RANGE)
                && FuncUtils.equals(monster.getMoveStatus(), MoveStatus.STANDING)) {
            log.debug("Monster {} start move to {} {}", monster.getId(),
                    monster.getTarget().getRoleType(), monster.getTarget().getId());
            monster.setMoveStatus(MoveStatus.MOVEING);
        }
        if (FuncUtils.notEquals(monster.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            monster.setMoveStatus(MoveStatus.STANDING);
        }
        if (FuncUtils.equals(monster.getMoveStatus(), MoveStatus.MOVEING)) {
            rootMap.roleMoveToTargetInTick(monster);
        }
    }
}
