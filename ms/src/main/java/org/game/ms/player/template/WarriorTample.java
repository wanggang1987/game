/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.player.template;

import javax.annotation.PostConstruct;
import org.game.ms.config.YamlPropertySourceFactory;
import lombok.Data;
import org.game.ms.skill.warrior.Charge;
import org.game.ms.skill.warrior.Rend;
import org.springframework.beans.factory.annotation.Autowired;
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
@ConfigurationProperties(prefix = "player.warrior")
public class WarriorTample extends PlayerTemplate {

    @Autowired
    private Rend rend;
    @Autowired
    private Charge charge;

    @PostConstruct
    private void init() {
        skills.add(rend);
        skills.add(charge);
    }
}
