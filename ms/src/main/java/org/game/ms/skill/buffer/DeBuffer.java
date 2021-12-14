/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import java.sql.Timestamp;
import org.game.ms.role.Role;
import org.game.ms.skill.Skill;

/**
 *
 * @author wanggang
 */
public class DeBuffer {

    private Skill skill;
    private Role source;
    private Role target;
    private Timestamp startTime;
    private Timestamp endTime;
}
