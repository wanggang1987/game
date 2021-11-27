/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author wanggang
 */
@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
@EnableScheduling
public class Boot {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Boot.class, args);
    }
}
