/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.Role;
import org.game.ms.skill.NormalAttack;
import org.game.ms.skill.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class FightService {

    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private NormalAttack normalAttack;
    @Autowired
    private BattleService battleService;

    public void fight(Role role) {
        Role target = lifeCycle.getRole(role.getTargetType(), role.getTargetId());
        normalAttack(role, target);
    }

    private void normalAttack(Role role, Role target) {
        attackRanageCompare(role, target);
        if (FuncUtils.equals(role.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            return;
        }
        if (FuncUtils.numberCompare(role.getAttackCooldown(), 0) == 0) {
            double damage = skillDamageCaculate(role, normalAttack, target);
            damageTarget(role, damage, normalAttack, target);
            role.setAttackCooldown(role.getAttackCooldownMax());
        }
    }

    private static void attackRanageCompare(Role source, Role target) {
        double xDistance = target.getLocation().getX() - source.getLocation().getX();
        double yDistance = target.getLocation().getY() - source.getLocation().getY();
        double preDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        if (FuncUtils.numberCompare(preDistance, source.getAttackRange()) == 1) {
            source.setAttackStatus(AttackStatus.OUT_RANGE);
        } else {
            source.setAttackStatus(AttackStatus.AUTO_ATTACK);
        }
    }

    private double skillDamageCaculate(Role role, Skill skill, Role target) {
        double damage = role.getAttackPower() * skill.getAttackPowerRate() + role.getAttack() - target.getDefense();
        if (damage < 1) {
            damage = 1;
        }
        return FuncUtils.randomInPersentRange(damage, 30);
    }

    private void damageTarget(Role role, double damage, Skill skill, Role target) {
        target.setHealthPoint(target.getHealthPoint() - damage);
        log.debug("{} {} {} {} {} damage {} health {}/{}", role.getRoleType(), role.getId(), skill.getName(),
                target.getRoleType(), target.getId(), damage,
                target.getHealthPoint(), target.getHealthMax());
        battleService.addFightStatus(role, target);
    }

}
