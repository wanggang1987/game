/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.player.template;

import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
public class PlayerTemplate {

    private double speed;
    private double attackRange;
    private double attackCooldown;
    private double baseHealth;
    private double baseResource;
    private double baseAttack;
    private double baseDeffence;
    private double growthHealth;
    private double growthResource;
    private double growthAttack;
    private double growthDefense;

}
