/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.warrior;

import lombok.Data;
import org.game.ms.skill.DamageType;
import org.game.ms.skill.LoopDamage;
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
public class Rend extends Skill {

    private String name = "撕裂";
    private ResourceType resourceType = ResourceType.ANGER;
    private int cost = 40;
    private double coolDown;
    private double coolDownMax = 9000;
    private SkillType skillType = SkillType.DAMAGE_SKILL;
    private RangeType rangeType = RangeType.MELEE;
    private LoopDamage loopDamage = new LoopDamage(9, 3, DamageType.PHYSICAL, 1, 0);
}
