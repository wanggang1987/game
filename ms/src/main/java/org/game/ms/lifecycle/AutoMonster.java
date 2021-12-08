/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.fight.FightService;
import org.game.ms.monster.Monster;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.LivingStatus;
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

    private void MonsterAuto(Monster monster) {
        if (monster.getTarget() == null && monster.getBattle() != null) {
            monster.setTarget(lifeCycle.onlinePlayer(monster.getBattle().getPlayers().stream().findAny().orElse(null)));
        }
        if (monster.getTarget() == null) {
            return;
        }
        if (LivingStatus.DEAD.equals(monster.getTarget().getLivingStatus())) {
            monster.setTarget(null);
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
        if (AttackStatus.OUT_RANGE.equals(monster.getAttackStatus())
                && MoveStatus.STANDING.equals(monster.getMoveStatus())) {
            log.debug("Monster {} start move to location {}", monster.getId(), monster.getTarget().getLocation());
            monster.setMoveStatus(MoveStatus.MOVEING);
        }
        if (!AttackStatus.OUT_RANGE.equals(monster.getAttackStatus())) {
            monster.setMoveStatus(MoveStatus.STANDING);
        }
        if (MoveStatus.MOVEING.equals(monster.getMoveStatus())) {
            monster.getMap().roleMoveToTargetInTick(monster);
        }
    }
}
