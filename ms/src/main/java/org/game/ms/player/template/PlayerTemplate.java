/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.player.template;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.game.ms.config.YamlPropertySourceFactory;
import org.game.ms.skill.Skill;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author wanggang
 */
@Data
@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:player.yaml")
@ConfigurationProperties(prefix = "player")
public class PlayerTemplate {

    private String name;
    private double speed;
    private double attackRange;
    private double attackCooldown;
    private int staminaGrow;
    private int strengtGrow;
    private int agilityGrow;
    private int intellectGrow;
    private int spiritGrow;

    private double dodgeRate;
    private double parryRate;
    private double citicalRate;

    protected List<Skill> skills = new ArrayList<>();

}
