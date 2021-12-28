/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.game.ms.role.Role;
import org.game.ms.skill.Skill;

/**
 *
 * @author wanggang
 */
@Data
@AllArgsConstructor
public class Buffer {

    private long id;
    @JsonIgnore
    private Role source;
    @JsonIgnore
    private Role target;
    private Skill skill;
}
