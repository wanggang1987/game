/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import org.game.ms.func.FuncUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.player.Player;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Service
public class FightService {

    @Autowired
    private LifeCycle lifeCycle;

    public void fight(Player player) {
        attackRanageCompare(player);
        if (AttackStatus.AUTO_ATTACK.equals(player.getAttackStatus())) {
            player.getTarget().setLivingStatus(LivingStatus.DEAD);
            lifeCycle.detoryMonster(player.getTarget());
        }
    }

    public static void attackRanageCompare(Role source) {
        double xDistance = source.getTarget().getLocation().getX() - source.getLocation().getX();
        double yDistance = source.getTarget().getLocation().getY() - source.getLocation().getY();
        double preDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        if (FuncUtils.distanceCompare(preDistance, source.getAttackRange()) == 1) {
            source.setAttackStatus(AttackStatus.OUT_RANGE);
        } else {
            source.setAttackStatus(AttackStatus.AUTO_ATTACK);
        }
    }
}
