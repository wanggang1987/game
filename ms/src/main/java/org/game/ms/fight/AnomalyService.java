/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import java.util.ArrayList;
import java.util.List;
import org.game.ms.func.FuncUtils;
import org.game.ms.map.RootMap;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.Role;
import org.game.ms.skill.AnomalyStatus;
import org.game.ms.skill.buffer.Buffer;
import org.game.ms.skill.buffer.BufferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Service
public class AnomalyService {

    @Autowired
    private RootMap rootMap;
    @Autowired
    private BufferService bufferService;

    public boolean anomalyPass(Role role) {
        List<Buffer> removeDebuffers = new ArrayList<>();

        for (Buffer debuffer : role.getDeBuffers()) {
            if (FuncUtils.equals(debuffer.getSkill().getAnomalyStatus(), AnomalyStatus.CHARGING)) {
                if (FuncUtils.equals(role.getMoveStatus(), MoveStatus.STANDING)) {
                    removeDebuffers.add(debuffer);
                } else if (FuncUtils.equals(role.getMoveStatus(), MoveStatus.MOVEING)) {
                    rootMap.roleChargeToTargetInTick(role);
                    return true;
                }
            }
        }

        removeDebuffers.forEach(debuffer -> bufferService.removeBuffer(debuffer));
        return false;
    }

}
