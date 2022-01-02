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
import org.springframework.stereotype.Service;

/**
 *
 * @author gangwang
 */
@Slf4j
@Service
public class RoleService {
    
    public void updateSpeed(Role role) {
        int persent = 100;
        for (Buffer buffer : role.getBuffers().getDeBuffers()) {
            ControlEffect controlEffect = buffer.getControl();
            if (FuncUtils.notEmpty(controlEffect)
                    && FuncUtils.equals(controlEffect.getAnomalyStatus(), AnomalyStatus.SPEED_DOWN)) {
                persent -= controlEffect.getPersent();
            }
        }
        role.setFinalSpeed(role.getBaseSpeed() * persent / 100);
        log.debug("updateSpeed {} {} {}", role.getRoleType(), role.getId(), role.getFinalSpeed());
    }
}
