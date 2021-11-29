/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.monster.template;

import org.game.ms.monster.template.MonsterTemplate;
import lombok.Data;
import org.game.ms.config.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author gangwang
 */
@Data
@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:initdata.yaml")
@ConfigurationProperties(prefix = "monster.wolf")
public class WolfTemplate extends MonsterTemplate{
    
}
