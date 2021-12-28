/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.game.ms.player.Profession;
import org.game.ms.skill.resource.ResourceType;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Data
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class Skill {

    private String name;
    private Profession profession;
    private SkillType skillType;
    private ResourceType resourceType;
    private int cost;
    private double coolDown;
    private double coolDownMax;
    private int rangeMin;
    private int rangeMax;
    private RangeType rangeType;
    private DirectDamage directDamage;
    private LoopDamage loopDamage;
    private AnomalyStatus anomalyStatus;
}
