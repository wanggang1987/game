/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.MessageService;
import org.game.ms.func.FuncUtils;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.LivingStatus;
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
    private NormalAttack normalAttack;
    @Autowired
    private BattleService battleService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private BufferService bufferService;
    @Autowired
    private MessageService messageService;

    public void autoFight(Role role) {
        attackRanageCompare(role);
        physicalAttack(role);
    }

    private void physicalAttack(Role role) {
        if (FuncUtils.equals(role.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            return;
        }

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
                double damage = skillDamageCaculate(role, skill.getDirectDamage());
                damageTarget(role, damage, skill, role.getTarget());
            }
            if (FuncUtils.notEmpty(skill.getLoopDamage())) {
                Buffer deBuffer = bufferService.createBuffer(role, skill, true);
                if (role.getTarget().getBuffers().contains(deBuffer)) {
                    continue;
                }
                bufferService.addBuffer(deBuffer);

                LoopDamage loopDamage = skill.getLoopDamage();
                double damage = skillDamageCaculate(role, loopDamage);
                int n = loopDamage.getLastTime() / loopDamage.getLoopTime();
                for (int i = 1; i <= n; i++) {
                    taskService.addTask(new LoopDamageTask(deBuffer, damage / n, i * loopDamage.getLoopTime()));
                }
                taskService.addTask(new BufferManagerTask(deBuffer, false, loopDamage.getLastTime()));
            }
            resourceService.skillCoolDownBegin(role.getResource(), skill);
            log.debug("{} {} cast skill {} to {} {}",
                    role.getRoleType(), role.getId(), skill.getName(), role.getTarget().getRoleType(), role.getTarget().getId());
        }

        normalAttack(role);
    }

    private void normalAttack(Role role) {
        if (resourceService.attackCoolDownReady(role.getResource())) {
            double damage = skillDamageCaculate(role, normalAttack.getDirectDamage());
            damageTarget(role, damage, normalAttack, role.getTarget());
            resourceService.attackCoolDownBegin(role.getResource());
            resourceService.gainAngerByHit(role.getResource());
            messageService.addCastSkill(role, normalAttack);
        }
    }

    private static double targetDistance(Role role) {
        double xDistance = role.getTarget().getLocation().getX() - role.getLocation().getX();
        double yDistance = role.getTarget().getLocation().getY() - role.getLocation().getY();
        return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
    }

    private static void attackRanageCompare(Role role) {
        if (targetDistance(role) > role.getAttackRange()) {
            role.setAttackStatus(AttackStatus.OUT_RANGE);
        } else {
            role.setAttackStatus(AttackStatus.AUTO_ATTACK);
        }
    }

    private double skillDamageCaculate(Role role, DamageBase damageBase) {
        double damage = role.getAttackPower() * damageBase.getAttackPowerRate() + role.getAttack() - role.getTarget().getDefense();
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
        messageService.addFightDamage(role, damage, skill, target);
        messageService.getFightStatus().add(target);
    }

    public void loopDamage(LoopDamageTask task) {
        Buffer buffer = task.getBuffer();
        Role source = buffer.getSource();
        Role target = buffer.getTarget();
        if (FuncUtils.notEmpty(target)
                && FuncUtils.equals(target.getLivingStatus(), LivingStatus.LIVING)
                && target.getBuffers().contains(buffer)) {
            damageTarget(source, task.getDamage(), buffer.getSkill(), target);
        }
    }

}
