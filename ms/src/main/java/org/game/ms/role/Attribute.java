/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attribute {

    private double baseAttackPower;
    private double finalAttackPower;
    private double magicPower;

    private int stamina;
    private int strengt;
    private int agility;
    private int intellect;
    private int spirit;
    //闪避
    private double dodge;
    //招架
    private double parry;
    //暴击
    private double critical;
    //急速
    private double haste;
    //精通
    private double mastery;
    //全能
    private double versatility;
}
