/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.map.RootMap;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.Role;
import org.game.ms.role.RoleService;
import org.game.ms.skill.EffectStatus;
import org.game.ms.skill.buffer.Buffer;
import org.game.ms.timeline.BufferManagerTask;
import org.game.ms.timeline.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class BufferEffectService {
    
    @Autowired
    private RootMap rootMap;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private FightService fightService;
    
    public boolean anomalyPass(Role role) {
        for (Buffer buffer : role.getBuffers().getDeBuffers()) {
            if (FuncUtils.isEmpty(buffer.getEffect())) {
                continue;
            }
            if (FuncUtils.equals(buffer.getEffect().getEffectStatus(), EffectStatus.CHARGING)) {
                if (FuncUtils.equals(role.getMoveStatus(), MoveStatus.STANDING)) {
                    taskService.addTask(new BufferManagerTask(buffer, false, 0));
                } else if (FuncUtils.equals(role.getMoveStatus(), MoveStatus.MOVEING)) {
                    rootMap.roleChargeToTargetInTick(role);
                    return true;
                }
            } else if (FuncUtils.equals(buffer.getEffect().getEffectStatus(), EffectStatus.DIZZINESS)) {
                return true;
            }
        }
        return false;
    }
    
    public void attributeUpdate(Buffer buffer) {
        if (FuncUtils.isEmpty(buffer.getEffect())) {
            return;
        }
        if (FuncUtils.equals(buffer.getEffect().getEffectStatus(), EffectStatus.SPEED)) {
            roleService.updateSpeed(buffer.getTarget());
        } else if (FuncUtils.equals(buffer.getEffect().getEffectStatus(), EffectStatus.ATTACK_POWER)) {
            roleService.updateAttackPower(buffer.getTarget());
        } else if (FuncUtils.equals(buffer.getEffect().getEffectStatus(), EffectStatus.HURT)) {
            roleService.updateHurt(buffer.getTarget());
        }
    }
    
    public void attackFeedBack(Role role, Role target) {
        for (Buffer buffer : target.getBuffers().getBuffers()) {
            if (FuncUtils.isEmpty(buffer.getEffect())) {
                continue;
            }
            if (FuncUtils.equals(buffer.getEffect().getEffectStatus(), EffectStatus.COUNTER_ATTACK)
                    && buffer.getEffect().getCount() > 0
                    && fightService.counterAttack(target, role)) {
                buffer.getEffect().setCount(buffer.getEffect().getCount() - 1);
            }
        }
    }
}
