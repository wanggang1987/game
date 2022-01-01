/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.role.Role;
import org.game.ms.skill.AnomalyStatus;
import org.game.ms.skill.Skill;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class BufferService {

    public void addBuffer(Buffer buffer) {
        Buffers buffers = buffer.getTarget().getBuffers();
        if (FuncUtils.equals(buffer.getType(), BufferType.BUFFER)) {
            if (buffers.getBuffers().contains(buffer)) {
                return;
            }
            buffers.getBuffers().add(buffer);
            log.debug("addBuffer {} {} {}", buffer.getSource().getId(), buffer.getSkill().getName(), buffer.getTarget().getId());
        } else if (FuncUtils.equals(buffer.getType(), BufferType.DE_BUFFER)) {
            if (buffers.getDeBuffers().contains(buffer)) {
                return;
            }
            buffers.getDeBuffers().add(buffer);
            log.debug("addDeBuffer {} {} {}", buffer.getSource().getId(), buffer.getSkill().getName(), buffer.getTarget().getId());
        } else if (FuncUtils.equals(buffer.getType(), BufferType.ANOMALY)) {
            if (buffers.getAnomalies().contains(buffer)) {
                return;
            }
            buffers.getAnomalies().add(buffer);
            log.debug("addAnomaly {} {} {}", buffer.getSource().getId(), buffer.getSkill().getName(), buffer.getTarget().getId());
        }
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
        } else if (FuncUtils.equals(buffer.getType(), BufferType.ANOMALY)) {
            removeBuffer(buffers.getAnomalies(), buffer);
            log.debug("removeAnomaly {} {} {}", buffer.getSource().getId(), buffer.getSkill().getName(), buffer.getTarget().getId());
        }
    }

    public Buffer createBuffer(Role source, Role target, Skill skill, BufferType type) {
        Buffer buffer = new Buffer();
        buffer.setSource(source);
        buffer.setTarget(target);
        buffer.setSkill(skill);
        buffer.setType(type);
        return buffer;
    }

    public Buffer createBuffer(Role source, Role target, Skill skill, AnomalyStatus status) {
        Buffer buffer = new Buffer();
        buffer.setSource(source);
        buffer.setTarget(target);
        buffer.setSkill(skill);
        buffer.setType(BufferType.ANOMALY);
        buffer.setAnomalyStatus(status);
        return buffer;
    }

    public boolean containsBuffer(Buffer buffer) {
        Buffers buffers = buffer.getTarget().getBuffers();
        if (FuncUtils.equals(buffer.getType(), BufferType.BUFFER)) {
            return buffers.getBuffers().contains(buffer);
        } else if (FuncUtils.equals(buffer.getType(), BufferType.DE_BUFFER)) {
            return buffers.getDeBuffers().contains(buffer);
        } else if (FuncUtils.equals(buffer.getType(), BufferType.ANOMALY)) {
            return buffers.getAnomalies().contains(buffer);
        }
        return false;
    }

    public void clear(Buffers buffers) {
        buffers.getBuffers().clear();
        buffers.getDeBuffers().clear();
        buffers.getAnomalies().clear();
    }

}
