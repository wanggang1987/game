/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.id.IdService;
import org.game.ms.lifecycle.LifeCycle;
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
    private LifeCycle lifeCycle;
    @Autowired
    private IdService idService;
    
    public void addBuffer(Buffer buffer) {
        Role target = lifeCycle.getRole(buffer.getTargetType(), buffer.getTargetId());
        if (target.getBuffers().contains(buffer)) {
            return;
        }
        target.getBuffers().add(buffer);
        log.debug("addBuffer {}", buffer);
    }
    
    public void removeBuffer(Buffer buffer) {
        Role target = lifeCycle.getRole(buffer.getTargetType(), buffer.getTargetId());
        if (FuncUtils.notEmpty(target)) {
            target.getBuffers().remove(buffer);
            log.debug("removeBuffer {}", buffer);
        }
    }
    
    public Buffer createBuffer(Role source, Role target, Skill skill, boolean isBuffer) {
        return new Buffer(source.getId(), source.getRoleType(),
                target.getId(), target.getRoleType(), skill, isBuffer);
    }
    
}
