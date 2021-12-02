/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.player.Player;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.FightingStatus;
import org.game.ms.role.LivingStatus;
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

    public void fight(Player player) {
        normalAttack(player);
    }

    public void normalAttack(Player player) {
        attackRanageCompare(player);
        if (AttackStatus.OUT_RANGE.equals(player.getAttackStatus())) {
            return;
        }
        if (FuncUtils.numberCompare(player.getAttackCooldown(), 0) == 0) {
            double damage = skillDamageCaculate(player, normalAttack);
            playerDamageMonster(player, damage, normalAttack);
            player.setAttackCooldown(player.getAttackCooldownMax());
        }
        player.setFightingStatus(FightingStatus.IN_FIGHTING);
        player.getTarget().setFightingStatus(FightingStatus.IN_FIGHTING);
        if (monsterDie(player.getTarget())) {
            player.setFightingStatus(FightingStatus.NOT_FIGHTING);
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

    private double skillDamageCaculate(Player player, Skill skill) {
        double damage = player.getAttackPower() * skill.getAttackPowerRate() + player.getAttack() - player.getTarget().getDefense();
        return FuncUtils.randomInPersentRange(damage, 30);
    }

    private boolean monsterDie(Role role) {
        if (FuncUtils.numberCompare(role.getHealthPoint(), 0.5) == -1) {
            log.debug("role {} die", role.getId());
            role.setLivingStatus(LivingStatus.DEAD);
            lifeCycle.detoryMonster(role);
            return true;
        }
        return false;
    }

    private void playerDamageMonster(Player player, double damage, Skill skill) {
        player.getTarget().setHealthPoint(player.getTarget().getHealthPoint() - damage);
        log.debug("player {} {} monster {} damage {}", player.getId(), skill.getName(), player.getTarget().getId(), damage);
    }

}
