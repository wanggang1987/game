/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import org.game.ms.lifecycle.LifeCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Service
public class RoleService {

    @Autowired
    private LifeCycle lifeCycle;

    public void moveToTargetInTick(Role role) {
        Role target = lifeCycle.getRole(role.getTargetType(), role.getTargetId());
        role.getMap().roleMoveToTargetInTick(role, target);
    }

}
