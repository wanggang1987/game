/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource {

    private double attackCooldown;
    private double attackCooldownMax;
    private double skillCooldown;
    private double skillCooldownMax;

    private double angerPoint;
    private double angerMax;
}
