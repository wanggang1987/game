/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.warrior;

import lombok.Data;
import org.game.ms.role.MoveStatus;
import org.game.ms.skill.RangeType;
import org.game.ms.skill.Skill;
import org.game.ms.skill.SkillType;
import org.game.ms.skill.resource.ResourceType;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Data
@Component
public class Charge extends Skill {

    private String name = "冲锋";
    private SkillType skillType = SkillType.MOVE_SKILL;
    private ResourceType resourceType = ResourceType.ANGER;
    private int cost = -25;
    private double coolDownMax = 30;
    private int rangeMin = 5;
    private int rangeMax = 25;
    private RangeType rangeType = RangeType.REMOTE;
    private MoveStatus moveStatus = MoveStatus.CHARGING;
}
