/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.game.ms.role.RoleType;
import org.game.ms.skill.SkillType;

/**
 *
 * @author gangwang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CastSkillMsg {

    private long sourceId;
    private RoleType sourceType;
    private long targetId;
    private RoleType targetType;
    private long skillId;
    private SkillType skillType;
    private String skillName;
    private double targetX;
    private double targetY;
}
