/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.resource;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.player.Player;
import org.game.ms.role.Role;
import org.game.ms.skill.Skill;
import org.game.ms.timeline.WheelConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class ResourceService {

    private final double angerLosePerSeond = 1;
    private final double angerGainPerHit = 20;

    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private WheelConfig wheelConfig;

    public boolean attackCoolDownReady(Resource resource) {
        return FuncUtils.numberCompare(resource.getAttackCooldown(), 0) == 0;
    }

    public void attackCoolDownBegin(Resource resource) {
        resource.setAttackCooldown(resource.getAttackCooldownMax());
    }

    public boolean generalSkillCoolDownReady(Resource resource) {
        return FuncUtils.numberCompare(resource.getSkillCooldown(), 0) == 0;
    }

    public boolean skillCoolDownReady(Skill skill) {
        return FuncUtils.numberCompare(skill.getCoolDown(), 0) == 0;
    }

    public void skillCoolDownBegin(Resource resource, Skill skill) {
        resource.setSkillCooldown(resource.getSkillCooldownMax());
        skill.setCoolDown(skill.getCoolDownMax());
    }

    public void gainAngerByHit(Resource resource) {
        resource.setAngerPoint(resource.getAngerPoint() + angerGainPerHit);
        if (resource.getAngerPoint() > resource.getAngerMax()) {
            resource.setAngerPoint(resource.getAngerMax());
        }
    }

    public boolean skillCostResource(Resource resource, Skill skill) {
        if (FuncUtils.equals(skill.getResourceType(), ResourceType.MAGIC)) {
        } else if (FuncUtils.equals(skill.getResourceType(), ResourceType.ANGER)
                && resource.getAngerPoint() > skill.getCost()) {
            resource.setAngerPoint(resource.getAngerPoint() - skill.getCost());
            return true;
        }
        return false;
    }

    private void coolDownForTick(Role role) {
        Resource resource = role.getResource();
        if (resource.getAttackCooldown() > 0) {
            double cd = resource.getAttackCooldown() - wheelConfig.getTickDuration();
            resource.setAttackCooldown(cd < 0 ? 0 : cd);
        }
        if (resource.getSkillCooldown() > 0) {
            double cd = resource.getSkillCooldown() - wheelConfig.getTickDuration();
            resource.setSkillCooldown(cd < 0 ? 0 : cd);
        }
        role.getSkills().stream()
                .filter(skill -> skill.getCoolDown() > 0)
                .forEach(skill -> {
                    double cd = skill.getCoolDown() - wheelConfig.getTickDuration();
                    skill.setCoolDown(cd < 0 ? 0 : cd);
                });
    }

    private void resourceForTick(Player player) {
        Resource resource = player.getResource();
        if (player.getBattle() == null && resource.getAngerPoint() > 0) {
            resource.setAngerPoint(resource.getAngerPoint() - angerLosePerSeond / 1000 * wheelConfig.getTickDuration());
            if (resource.getAngerPoint() < 0) {
                resource.setAngerPoint(0);
            }
        }
    }

    public void resourceUpdateForTick() {
        lifeCycle.onlinePlayers().stream()
                .forEach(player -> {
                    coolDownForTick(player);
                    resourceForTick(player);
                });
        lifeCycle.onlineMonsters().stream()
                .forEach(monster -> {
                    coolDownForTick(monster);
                });
    }
}
