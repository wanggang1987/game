/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role.template;

import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
public class RoleTemplate {
    
    private Double baseHealth;
    private Double baseResource;
    private Double baseAttack;
    private Double baseDeffence;
    private Double growthHealth;
    private Double growthResource;
    private Double growthAttack;
    private Double growthDefense;

}