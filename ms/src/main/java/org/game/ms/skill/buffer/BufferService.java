/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.fight.AnomalyService;
import org.game.ms.func.FuncUtils;
import org.game.ms.role.Role;
import org.game.ms.skill.ControlEffect;
import org.game.ms.skill.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class BufferService {

    @Autowired
    private AnomalyService anomalyService;

    public void addBuffer(Buffer buffer) {
        Buffers buffers = buffer.getTarget().getBuffers();
        if (containsBuffer(buffer)) {
            return;
        }
        if (FuncUtils.equals(buffer.getType(), BufferType.BUFFER)) {
            buffers.getBuffers().add(buffer);
            log.debug("addBuffer {} {} {}", buffer.getSource().getId(), buffer.getSkill().getName(), buffer.getTarget().getId());
        } else if (FuncUtils.equals(buffer.getType(), BufferType.DE_BUFFER)) {
            buffers.getDeBuffers().add(buffer);
            log.debug("addDeBuffer {} {} {}", buffer.getSource().getId(), buffer.getSkill().getName(), buffer.getTarget().getId());
        }
        anomalyService.attributeUpdate(buffer);
    }

    private void removeBuffer(List<Buffer> list, Buffer buffer) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Buffer next = (Buffer) it.next();
            if (FuncUtils.equals(next.getSource().getId(), buffer.getSource().getId())
                    && FuncUtils.equals(next.getTarget().getId(), buffer.getTarget().getId())
                    && FuncUtils.equals(next.getSkill().getId(), buffer.getSkill().getId())) {
                it.remove();
                break;
            }
        }
    }

    public void removeBuffer(Buffer buffer) {
        Buffers buffers = buffer.getTarget().getBuffers();
        if (FuncUtils.equals(buffer.getType(), BufferType.BUFFER)) {
            removeBuffer(buffers.getBuffers(), buffer);
            log.debug("removeBuffer {} {} {}", buffer.getSource().getId(), buffer.getSkill().getName(), buffer.getTarget().getId());
        } else if (FuncUtils.equals(buffer.getType(), BufferType.DE_BUFFER)) {
            removeBuffer(buffers.getDeBuffers(), buffer);
            log.debug("removeDeBuffer {} {} {}", buffer.getSource().getId(), buffer.getSkill().getName(), buffer.getTarget().getId());
        }
        anomalyService.attributeUpdate(buffer);
    }

    public Buffer createBuffer(Role source, Role target, Skill skill, BufferType type) {
        Buffer buffer = new Buffer();
        buffer.setSource(source);
        buffer.setTarget(target);
        buffer.setSkill(skill);
        buffer.setType(type);
        return buffer;
    }

    public Buffer createBuffer(Role source, Role target, Skill skill, BufferType type, ControlEffect control) {
        Buffer buffer = new Buffer();
        buffer.setSource(source);
        buffer.setTarget(target);
        buffer.setSkill(skill);
        buffer.setType(type);
        buffer.setControl(control);
        return buffer;
    }

    public boolean containsBuffer(Buffer buffer) {
        Buffers buffers = buffer.getTarget().getBuffers();
        if (FuncUtils.equals(buffer.getType(), BufferType.BUFFER)) {
            for (Buffer next : buffers.getBuffers()) {
                if (FuncUtils.equals(next.getSource().getId(), buffer.getSource().getId())
                        && FuncUtils.equals(next.getTarget().getId(), buffer.getTarget().getId())
                        && FuncUtils.equals(next.getSkill().getId(), buffer.getSkill().getId())) {
                    return true;
                }
            }
        } else if (FuncUtils.equals(buffer.getType(), BufferType.DE_BUFFER)) {
            for (Buffer next : buffers.getDeBuffers()) {
                if (FuncUtils.equals(next.getSource().getId(), buffer.getSource().getId())
                        && FuncUtils.equals(next.getTarget().getId(), buffer.getTarget().getId())
                        && FuncUtils.equals(next.getSkill().getId(), buffer.getSkill().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clear(Buffers buffers) {
        buffers.getBuffers().clear();
        buffers.getDeBuffers().clear();
    }
}
