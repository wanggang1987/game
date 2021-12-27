/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.id.IdService;
import org.game.ms.role.Role;
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
    private IdService idService;

    public void addBuffer(Buffer buffer) {
        if (buffer.getTarget().getBuffers().contains(buffer)) {
            return;
        }
        buffer.getTarget().getBuffers().add(buffer);
        log.debug("addBuffer {}", buffer);
    }

    public void removeBuffer(Buffer buffer) {
        if (FuncUtils.notEmpty(buffer.getTarget())) {
            buffer.getTarget().getBuffers().remove(buffer);
            buffer.getTarget().getDeBuffers().remove(buffer);
            log.debug("removeBuffer {}", buffer);
        }
    }

    public void addDeBuffer(Buffer buffer) {
        if (buffer.getTarget().getDeBuffers().contains(buffer)) {
            return;
        }
        buffer.getTarget().getDeBuffers().add(buffer);
        log.debug("addDeBuffer {}", buffer);
    }

    public Buffer createBuffer(Role source, Role target, Skill skill) {
        return new Buffer(source, target, skill);
    }

}
