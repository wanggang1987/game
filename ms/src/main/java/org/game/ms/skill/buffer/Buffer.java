/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.game.ms.role.Role;
import org.game.ms.skill.ControlEffect;
import org.game.ms.skill.Skill;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Buffer {

    private BufferType type;
    private ControlEffect control;
    private Skill skill;
    @JsonIgnore
    private Role source;
    @JsonIgnore
    private Role target;

}
