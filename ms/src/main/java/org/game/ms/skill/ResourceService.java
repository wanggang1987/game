/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.player.Player;
import org.game.ms.role.Role;
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

    public boolean attackCoolDownReady(Role role) {
        return FuncUtils.numberCompare(role.getAttackCooldown(), 0) == 0;
    }

    public void attackCoolDownBegin(Role role) {
        role.setAttackCooldown(role.getAttackCooldownMax());
    }

    public void gainAngerByHit(Role role) {
        Resource resource = role.getResource();
        resource.setAngerPoint(resource.getAngerPoint() + angerGainPerHit);
        if (resource.getAngerPoint() > resource.getAngerMax()) {
            resource.setAngerPoint(resource.getAngerMax());
        }
    }

    private void coolDownForTick(Role role) {
        if (role.getAttackCooldown() > 0) {
            role.setAttackCooldown(role.getAttackCooldown() - wheelConfig.getTickDuration());
            if (role.getAttackCooldown() < 0) {
                role.setAttackCooldown(0);
            }
        }
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
