/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.role.Role;
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
        RoleBuffers buffers = buffer.getTarget().getBuffers();
        if (FuncUtils.equals(buffer.getType(), BufferType.BUFFER)) {
            if (buffers.getBuffers().contains(buffer)) {
                return;
            }
            buffers.getBuffers().add(buffer);
            log.debug("addBuffer {} ", buffer);
        } else if (FuncUtils.equals(buffer.getType(), BufferType.DE_BUFFER)) {
            if (buffers.getDeBuffers().contains(buffer)) {
                return;
            }
            buffers.getDeBuffers().add(buffer);
            log.debug("addDeBuffer {}", buffer);
        } else if (FuncUtils.equals(buffer.getType(), BufferType.ANOMALY)) {
            if (buffers.getAnomalies().contains(buffer)) {
                return;
            }
            buffers.getAnomalies().add(buffer);
            log.debug("addAnomaly {}", buffer);
        }
    }

    public void removeBuffer(Buffer buffer) {
        RoleBuffers buffers = buffer.getTarget().getBuffers();
        if (FuncUtils.equals(buffer.getType(), BufferType.BUFFER)) {
            buffers.getBuffers().remove(buffer);
            log.debug("removeBuffer {} ", buffer);
        } else if (FuncUtils.equals(buffer.getType(), BufferType.DE_BUFFER)) {
            buffers.getDeBuffers().remove(buffer);
            log.debug("removeDeBuffer {}", buffer);
        } else if (FuncUtils.equals(buffer.getType(), BufferType.ANOMALY)) {
            buffers.getAnomalies().remove(buffer);
            log.debug("removeAnomaly {}", buffer);
        }
    }

    public Buffer createBuffer(Role source, Role target, Skill skill, BufferType type) {
        return new Buffer(type, skill, source, target);
    }

    public boolean containsBuffer(Buffer buffer) {
        RoleBuffers buffers = buffer.getTarget().getBuffers();
        if (FuncUtils.equals(buffer.getType(), BufferType.BUFFER)) {
            return buffers.getBuffers().contains(buffer);
        } else if (FuncUtils.equals(buffer.getType(), BufferType.DE_BUFFER)) {
            return buffers.getDeBuffers().contains(buffer);
        } else if (FuncUtils.equals(buffer.getType(), BufferType.ANOMALY)) {
            return buffers.getAnomalies().contains(buffer);
        }
        return false;
    }

    public void clear(RoleBuffers buffers) {
        buffers.getBuffers().clear();
        buffers.getDeBuffers().clear();
        buffers.getAnomalies().clear();
    }

}
