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
import org.game.ms.skill.AnomalyStatus;
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
public class AnomalyService {

    @Autowired
    private RootMap rootMap;
    @Autowired
    private TaskService taskService;

    public boolean anomalyPass(Role role) {
        for (Buffer buffer : role.getBuffers().getAnomalies()) {
            if (FuncUtils.equals(buffer.getAnomalyStatus(), AnomalyStatus.CHARGING)) {
                if (FuncUtils.equals(role.getMoveStatus(), MoveStatus.STANDING)) {
                    taskService.addTask(new BufferManagerTask(buffer, false, 0));
                } else if (FuncUtils.equals(role.getMoveStatus(), MoveStatus.MOVEING)) {
                    rootMap.roleChargeToTargetInTick(role);
                    return true;
                }
            } else if (FuncUtils.equals(buffer.getAnomalyStatus(), AnomalyStatus.DIZZINESS)) {
                return true;
            }
        }

        return false;
    }

}
