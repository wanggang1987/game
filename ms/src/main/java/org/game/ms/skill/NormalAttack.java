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
public class NormalAttack extends Skill {

    private String name = "普通攻击";
    private double attackPowerRate = 1.0;
    private double magicPowerRate = 0;
}
