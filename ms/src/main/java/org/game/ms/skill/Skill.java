/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Data
@Component
public class Skill {

    private String name;
    private double range;
    private int lastTime;
    private int loopTime;
    private DamageType damageType;
    private double attackPowerRate;
    private double magicPowerRate;
}
