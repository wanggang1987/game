/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.warrior;

import lombok.Data;
import org.game.ms.skill.DamageType;
import org.game.ms.skill.Skill;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Data
@Component
public class Rend extends Skill {

    private String name = "撕裂";
    private int lastTime = 9;
    private int loopTime = 3;
    private DamageType damageType = DamageType.PHYSICAL;
    private double attackPowerRate = 1;
}
