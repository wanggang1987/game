/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.MessageService;
import org.game.ms.func.FuncUtils;
import org.game.ms.func.JsonUtils;
import org.game.ms.monster.Monster;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.Role;
import org.game.ms.role.Attribute;
import org.game.ms.skill.AnomalyStatus;
import org.game.ms.skill.DamageBase;
import org.game.ms.skill.EffectType;
import org.game.ms.skill.LoopDamage;
import org.game.ms.skill.RangeType;
import org.game.ms.skill.Skill;
import org.game.ms.skill.buffer.Buffer;
import org.game.ms.skill.buffer.BufferService;
import org.game.ms.skill.buffer.BufferType;
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
        skillAttack(role);
        normalAttack(role);
    }

    private void skillAttack(Role role) {
        for (Skill skill : role.getSkills()) {
            if (!resourceService.generalSkillCoolDownReady(role.getResource())) {
                break;
            }
            if (!resourceService.castSkillReady(role.getResource(), skill)) {
                continue;
            }
            if (castSkillEligible(role, skill) && castSkill(role, skill)) {
                resourceService.castSkill(role.getResource(), skill);
                messageService.addCastSkill(role, skill, role.getTarget());
                log.debug("{} {} cast skill {} to {} {}",
                        role.getRoleType(), role.getId(), skill.getName(), role.getTarget().getRoleType(),
                        role.getTarget().getId());
            }
        }
    }

    private boolean castSkillEligible(Role role, Skill skill) {
        if (FuncUtils.equals(skill.getRangeType(), RangeType.MELEE)
                && FuncUtils.equals(role.getAttackStatus(), AttackStatus.AUTO_ATTACK)) {
            return true;
        }
        if (FuncUtils.equals(skill.getRangeType(), RangeType.REMOTE)
                && skill.getRangeMax() > role.getTargetDistance()
                && skill.getRangeMin() < role.getTargetDistance()) {
            return true;
        }
        return false;
    }

    private boolean castSkill(Role role, Skill skill) {
        if (FuncUtils.equals(skill.getEffectType(), EffectType.SOURCE_RANGE)) {
            Collection<Monster> monsters = role.getMap().findMonstersInDistance(role, skill.getEffictRange());
            monsters.forEach(monster -> castSkill(role, skill, monster));
            return true;
        } else if (FuncUtils.equals(skill.getEffectType(), EffectType.SINGE)
                || FuncUtils.isEmpty(skill.getEffectType())) {
            Role target = role.getTarget();
            return castSkill(role, skill, target);
        }
        return false;
    }

    private boolean castSkill(Role source, Skill skill, Role target) {
        if (FuncUtils.notEmpty(skill.getDirectDamage())) {
            double damage = skillDamageCaculate(source, skill.getDirectDamage());
            damageTarget(source, damage, skill, target);
        }

        if (FuncUtils.notEmpty(skill.getLoopDamage())) {
            Buffer deBuffer = bufferService.createBuffer(source, target, skill, BufferType.DE_BUFFER);
            if (bufferService.containsBuffer(deBuffer)) {
                return false;
            }
            bufferService.addBuffer(deBuffer);

            LoopDamage loopDamage = skill.getLoopDamage();
            double damage = skillDamageCaculate(source, loopDamage);
            int n = loopDamage.getLastTime() / loopDamage.getLoopTime();
            for (int i = 1; i <= n; i++) {
                taskService.addTask(new LoopDamageTask(deBuffer, damage / n, i * loopDamage.getLoopTime()));
            }
            taskService.addTask(new BufferManagerTask(deBuffer, false, loopDamage.getLastTime()));
        }

        if (FuncUtils.notEmpty(skill.getSourceControl())) {
            Buffer buffer = bufferService.createBuffer(source, source, skill, BufferType.DE_BUFFER, skill.getSourceControl());
            if (bufferService.containsBuffer(buffer)) {
                return false;
            }
            bufferService.addBuffer(buffer);
            if (FuncUtils.equals(skill.getSourceControl().getAnomalyStatus(), AnomalyStatus.CHARGING)) {
                source.setMoveStatus(MoveStatus.MOVEING);
            }
        }

        if (FuncUtils.notEmpty(skill.getTargetControl())) {
            Buffer debuffer = bufferService.createBuffer(source, target, skill, BufferType.DE_BUFFER, skill.getTargetControl());
            if (bufferService.containsBuffer(debuffer)) {
                return false;
            }
            bufferService.addBuffer(debuffer);
            battleService.addFightStatus(source, target);
            taskService.addTask(new BufferManagerTask(debuffer, false, skill.getTargetControl().getLastTime()));
        }
        return true;
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
            messageService.addCastSkill(role, role.getNormalAttack(), role.getTarget());
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

    private double skillDamageCaculate(Role source, DamageBase damageBase) {
        Attribute sourceAttribute = source.getAttribute();
        double damage = sourceAttribute.getFinalAttackPower() * damageBase.getAttackPowerRate();
        if (damage < 1) {
            damage = 1;
        }
        return FuncUtils.randomInRangeByPersentage(damage, 30);
    }

    private FinalDamageType persentage(Attribute sourceAttribute, Attribute targetAttribute) {
        double persentage = FuncUtils.randomPersentage();
        if (persentage < targetAttribute.getDodge()) {
            return FinalDamageType.DODGE;
        }
        persentage -= targetAttribute.getDodge();
        if (persentage < targetAttribute.getParry()) {
            return FinalDamageType.PARRY;
        }
        persentage -= targetAttribute.getParry();
        if (persentage < sourceAttribute.getCritical()) {
            return FinalDamageType.CRITICAL;
        }
        persentage -= sourceAttribute.getCritical();
        return FinalDamageType.DIRECT;
    }

    private void damageTarget(Role source, double damage, Skill skill, Role target) {
        FinalDamageType type = persentage(source.getAttribute(), target.getAttribute());
        double finalDamage = 0;
        if (FuncUtils.equals(type, FinalDamageType.DODGE)) {
            finalDamage = 0;
        } else if (FuncUtils.equals(type, FinalDamageType.PARRY)) {
            finalDamage = damage / 2;
        } else if (FuncUtils.equals(type, FinalDamageType.CRITICAL)) {
            finalDamage = damage * 2;
        } else if (FuncUtils.equals(type, FinalDamageType.DIRECT)) {
            finalDamage = damage;
        }

        target.setHealthPoint(target.getHealthPoint() - finalDamage);
        log.debug("{} {} {} {} {} {} damage {} health {}/{}", source.getRoleType(), source.getId(), skill.getName(),
                target.getRoleType(), target.getId(), type, finalDamage,
                target.getHealthPoint(), target.getHealthMax());
        battleService.addFightStatus(source, target);
        messageService.addFightDamage(source, finalDamage, skill, target);
        messageService.getFightStatus().add(target);
    }

    public void loopDamage(LoopDamageTask task) {
        Buffer buffer = task.getBuffer();
        Role source = buffer.getSource();
        Role target = buffer.getTarget();
        if (FuncUtils.notEmpty(target)
                && FuncUtils.equals(target.getLivingStatus(), LivingStatus.LIVING)
                && bufferService.containsBuffer(buffer)) {
            damageTarget(source, task.getDamage(), buffer.getSkill(), target);
        }
    }

}
