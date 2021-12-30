package org.game.ms.client.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.game.ms.role.Role;
import org.game.ms.skill.Skill;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CastSkillRequest{
    
    private Role source;
    private Role target;
    private Skill skill;
}