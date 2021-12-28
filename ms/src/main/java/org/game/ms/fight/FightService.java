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
import org.game.ms.role.MoveStatus;
import org.game.ms.role.Role;
import org.game.ms.role.RoleAttribute;
import org.game.ms.skill.DamageBase;
import org.game.ms.skill.LoopDamage;
import org.game.ms.skill.RangeType;
import org.game.ms.skill.Skill;
import org.game.ms.skill.SkillType;
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
    private BattleService battleService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private BufferService bufferService;
    @Autowired
    private MessageService messageService;

    public void autoFight(Role role) {
        ranageCompare(role);
        meleeDamage(role);
        normalAttack(role);
        moveSkill(role);
    }

    private void moveSkill(Role role) {
        for (Skill skill : role.getSkills()) {
            if (!resourceService.generalSkillCoolDownReady(role.getResource())) {
                break;
            }
            if (FuncUtils.notEquals(skill.getSkillType(), SkillType.SOURCE_MOVE_SKILL)
                    || !resourceService.skillCoolDownReady(skill)) {
                continue;
            }
            if (!resourceService.skillCostResource(role.getResource(), skill)) {
                continue;
            }
            if (FuncUtils.equals(skill.getRangeType(), RangeType.REMOTE)
                    && skill.getRangeMax() > role.getTargetDistance()
                    && skill.getRangeMin() < role.getTargetDistance()) {
                Buffer buffer = bufferService.createBuffer(role, role, skill);
                bufferService.addDeBuffer(buffer);
                role.setMoveStatus(MoveStatus.MOVEING);
                resourceService.skillCoolDownBegin(role.getResource(), skill);
                log.debug("{} {} cast skill {} to {} {}",
                        role.getRoleType(), role.getId(), skill.getName(), role.getTarget().getRoleType(), role.getTarget().getId());
            }

        }
    }

    private void meleeDamage(Role role) {
        if (FuncUtils.equals(role.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            return;
        }
        for (Skill skill : role.getSkills()) {
            if (!resourceService.generalSkillCoolDownReady(role.getResource())) {
                break;
            }
            if (FuncUtils.notEquals(skill.getSkillType(), SkillType.DAMAGE_SKILL)
                    || FuncUtils.notEquals(skill.getRangeType(), RangeType.MELEE)
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
                Buffer deBuffer = bufferService.createBuffer(role, role.getTarget(), skill);
                if (role.getTarget().getDeBuffers().contains(deBuffer)) {
                    continue;
                }
                bufferService.addDeBuffer(deBuffer);

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

    }

    private void normalAttack(Role role) {
        if (FuncUtils.equals(role.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            return;
        }
        if (resourceService.attackCoolDownReady(role.getResource())) {
            double damage = skillDamageCaculate(role, role.getNormalAttack().getDirectDamage());
            damageTarget(role, damage, role.getNormalAttack(), role.getTarget());
            resourceService.attackCoolDownBegin(role.getResource());
            resourceService.gainResourceByHit(role.getResource());
            messageService.addCastSkill(role, role.getNormalAttack());
        }
    }

    private static void ranageCompare(Role role) {
        double xDistance = role.getLocation().getX() - role.getTarget().getLocation().getX();
        double yDistance = role.getLocation().getY() - role.getTarget().getLocation().getY();
        role.setTargetDistance(Math.sqrt(xDistance * xDistance + yDistance * yDistance));
        if (role.getTargetDistance() > role.getAttackRange()) {
            role.setAttackStatus(AttackStatus.OUT_RANGE);
        } else {
            role.setAttackStatus(AttackStatus.AUTO_ATTACK);
        }
    }

    private double skillDamageCaculate(Role role, DamageBase damageBase) {
        RoleAttribute roleAttribute = role.getRoleAttribute();
        double damage = roleAttribute.getAttackPower() * damageBase.getAttackPowerRate();
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
