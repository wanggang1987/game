/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Data
@Component
@PropertySource("classpath:initdata.yaml")
@ConfigurationProperties(prefix = "role.warrior")
public class WarriorTample extends RoleTemplate {

}
