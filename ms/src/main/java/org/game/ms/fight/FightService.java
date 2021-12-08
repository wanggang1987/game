/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
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
    private NormalAttack normalAttack;
    @Autowired
    private BattleService battleService;

    public void fight(Role role) {
        normalAttack(role);
    }

    public void normalAttack(Role role) {
        attackRanageCompare(role);
        if (AttackStatus.OUT_RANGE.equals(role.getAttackStatus())) {
            return;
        }
        if (FuncUtils.numberCompare(role.getAttackCooldown(), 0) == 0) {
            double damage = skillDamageCaculate(role, normalAttack);
            damageTarget(role, damage, normalAttack);
            role.setAttackCooldown(role.getAttackCooldownMax());
        }
    }

    public static void attackRanageCompare(Role source) {
        double xDistance = source.getTarget().getLocation().getX() - source.getLocation().getX();
        double yDistance = source.getTarget().getLocation().getY() - source.getLocation().getY();
        double preDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        if (FuncUtils.numberCompare(preDistance, source.getAttackRange()) == 1) {
            source.setAttackStatus(AttackStatus.OUT_RANGE);
        } else {
            source.setAttackStatus(AttackStatus.AUTO_ATTACK);
        }
    }

    private double skillDamageCaculate(Role role, Skill skill) {
        double damage = role.getAttackPower() * skill.getAttackPowerRate() + role.getAttack() - role.getTarget().getDefense();
        if (damage < 1) {
            damage = 1;
        }
        return FuncUtils.randomInPersentRange(damage, 30);
    }

    private void damageTarget(Role role, double damage, Skill skill) {
        role.getTarget().setHealthPoint(role.getTarget().getHealthPoint() - damage);
        log.debug("{} {} {} {} {} damage {}", role.getRoleType(), role.getId(), skill.getName(),
                role.getTarget().getRoleType(), role.getTarget().getId(), damage);
        battleService.addFightStatus(role, role.getTarget());
    }

}
