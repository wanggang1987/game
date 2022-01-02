/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.role;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.skill.AnomalyStatus;
import org.game.ms.skill.ControlEffect;
import org.game.ms.skill.buffer.Buffer;
import org.game.ms.skill.buffer.Buffers;
import org.springframework.stereotype.Service;

/**
 *
 * @author gangwang
 */
@Slf4j
@Service
public class RoleService {

    private double bufferCulate(Buffers buffers, AnomalyStatus anomalyStatus) {
        double persent = 1;
        for (Buffer buffer : buffers.getBuffers()) {
            ControlEffect controlEffect = buffer.getControl();
            if (FuncUtils.notEmpty(controlEffect)
                    && FuncUtils.equals(controlEffect.getAnomalyStatus(), anomalyStatus)) {
                persent *= (100 + controlEffect.getPersent()) / 100.0;
            }
        }
        for (Buffer buffer : buffers.getDeBuffers()) {
            ControlEffect controlEffect = buffer.getControl();
            if (FuncUtils.notEmpty(controlEffect)
                    && FuncUtils.equals(controlEffect.getAnomalyStatus(), anomalyStatus)) {
                persent *= (100 + controlEffect.getPersent()) / 100.0;
            }
        }
        return persent;
    }

    public void updateSpeed(Role role) {
        double persent = bufferCulate(role.getBuffers(), AnomalyStatus.SPEED);
        role.setFinalSpeed(role.getBaseSpeed() * persent);
        log.debug("updateSpeed {} {} {} {}", role.getRoleType(), role.getId(), persent, role.getFinalSpeed());
    }

    public void updateAttackPower(Role role) {
        double persent = bufferCulate(role.getBuffers(), AnomalyStatus.ATTACK_POWER);
        Attribute attribute = role.getAttribute();
        attribute.setFinalAttackPower(attribute.getBaseAttackPower() * persent);
        log.debug("updateAttackPower {} {} {} {}", role.getRoleType(), role.getId(), persent, attribute.getFinalAttackPower());
    }
}
