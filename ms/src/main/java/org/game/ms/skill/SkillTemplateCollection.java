/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill;

import java.util.List;
import lombok.Data;
import org.game.ms.config.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author wanggang
 */
@Data
@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:skill.yaml")
@ConfigurationProperties(prefix = "template")
public class SkillTemplateCollection {

    private List<Skill> skills;
}
