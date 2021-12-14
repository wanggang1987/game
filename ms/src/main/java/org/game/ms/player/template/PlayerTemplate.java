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
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:initdata.yaml")
@ConfigurationProperties(prefix = "player")
public class PlayerTemplate {

    private String name;
    private double speed;
    private double attackRange;
    private double attackCooldown;
    private double health;
    private double resource;
    private double attack;
    private double deffence;

    protected List<Skill> skills = new ArrayList<>();

}
