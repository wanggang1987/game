/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.timeline;

import lombok.Data;
import org.game.ms.role.Role;
import org.game.ms.skill.Skill;

/**
 *
 * @author wanggang
 */
@Data
public class TickTask {

    private Role source;
    private Role target;
    private Skill skill;
    private double damage;
    private long tick;
    private long ms;

    public TickTask(Role source, Role target, Skill skill, double damage, long ms) {
        this.source = source;
        this.target = target;
        this.skill = skill;
        this.damage = damage;
        this.ms = ms;
    }

}
