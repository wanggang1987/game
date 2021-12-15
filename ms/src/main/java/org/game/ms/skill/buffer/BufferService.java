/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Service
public class BufferService {

    @Autowired
    private LifeCycle lifeCycle;

    public void addBuffer(Buffer buffer) {
        Role target = lifeCycle.getRole(buffer.getTargetType(), buffer.getTargetId());
        if (target.getBuffers().contains(buffer)) {
            return;
        }
        target.getBuffers().add(buffer);
    }

    public void removeBuffer(Buffer buffer) {
        Role target = lifeCycle.getRole(buffer.getTargetType(), buffer.getTargetId());
        target.getBuffers().remove(buffer);
    }

}
