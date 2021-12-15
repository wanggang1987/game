/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import java.sql.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.Role;
import org.game.ms.skill.DamageBase;
import org.game.ms.skill.LoopDamage;
import org.game.ms.skill.NormalAttack;
import org.game.ms.skill.RangeType;
import org.game.ms.skill.Skill;
import org.game.ms.skill.buffer.Buffer;
import org.game.ms.skill.buffer.BufferService;
import org.game.ms.skill.resource.ResourceService;
import org.game.ms.timeline.BufferManagerTask;
import org.game.ms.timeline.LoopDamageTask;
import org.game.ms.timeline.TaskService;
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
    private TaskService taskService;
    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private NormalAttack normalAttack;
    @Autowired
    private BattleService battleService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private BufferService bufferService;

    public void autoFight(Role role) {
        Role target = lifeCycle.getRole(role.getTargetType(), role.getTargetId());
        attackRanageCompare(role, target);
        physicalAttack(role, target);
    }

    private void physicalAttack(Role role, Role target) {
        if (FuncUtils.equals(role.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            return;
        }
        normalAttack(role, target);

        for (Skill skill : role.getSkills()) {
            if (!resourceService.generalSkillCoolDownReady(role.getResource())) {
                break;
            }
            if (FuncUtils.notEquals(skill.getRangeType(), RangeType.MELEE)
                    || !resourceService.skillCoolDownReady(skill)) {
                continue;
            }
            if (!resourceService.skillCostResource(role.getResource(), skill)) {
                continue;
            }
            if (FuncUtils.notEmpty(skill.getDirectDamage())) {
                double damage = skillDamageCaculate(role, skill.getDirectDamage(), target);
                damageTarget(role, damage, skill, target);
            }
            if (FuncUtils.notEmpty(skill.getLoopDamage())) {
                LoopDamage loopDamage = skill.getLoopDamage();
                Timestamp now = FuncUtils.currentTime();
                Timestamp end = new Timestamp(now.getTime() + loopDamage.getLastTime());
                Buffer deBuffer = new Buffer(role.getId(), role.getRoleType(), target.getId(), target.getRoleType(),
                        skill, now, end, false);
                bufferService.addBuffer(deBuffer);

                double damage = skillDamageCaculate(role, loopDamage, target);
                int n = loopDamage.getLastTime() / loopDamage.getLoopTime();
                for (int i = 1; i <= n; i++) {
                    taskService.addTask(new LoopDamageTask(deBuffer, damage / n, i * loopDamage.getLoopTime()));
                }

                taskService.addTask(new BufferManagerTask(deBuffer, false, loopDamage.getLastTime()));
            }
            resourceService.skillCoolDownBegin(role.getResource(), skill);
        }

    }

    private void normalAttack(Role role, Role target) {
        if (resourceService.attackCoolDownReady(role.getResource())) {
            double damage = skillDamageCaculate(role, normalAttack.getDirectDamage(), target);
            damageTarget(role, damage, normalAttack, target);
            resourceService.attackCoolDownBegin(role.getResource());
            resourceService.gainAngerByHit(role.getResource());
        }
    }

    private static double targetDistance(Role source, Role target) {
        double xDistance = target.getLocation().getX() - source.getLocation().getX();
        double yDistance = target.getLocation().getY() - source.getLocation().getY();
        return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
    }

    private static void attackRanageCompare(Role source, Role target) {
        if (targetDistance(source, target) > source.getAttackRange()) {
            source.setAttackStatus(AttackStatus.OUT_RANGE);
        } else {
            source.setAttackStatus(AttackStatus.AUTO_ATTACK);
        }
    }

    private double skillDamageCaculate(Role role, DamageBase damageBase, Role target) {
        double damage = role.getAttackPower() * damageBase.getAttackPowerRate() + role.getAttack() - target.getDefense();
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

    public void loopDamage(LoopDamageTask task) {
        Buffer buffer = task.getBuffer();
        Role source = lifeCycle.getRole(buffer.getSourceType(), buffer.getSourceId());
        Role target = lifeCycle.getRole(buffer.getTargetType(), buffer.getTargetId());

        damageTarget(source, task.getDamage(), buffer.getSkill(), target);
    }

}
