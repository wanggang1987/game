/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.mock;

import cn.hutool.json.JSONUtil;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.role.Profession;
import org.game.ms.role.Role;
import org.game.ms.role.RoleCaculate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Slf4j
@Component
public class Mock {

    @Autowired
    private RoleCaculate roleCaculate;
    
    @PostConstruct
    private void init() {

        Role player = new Role();
        player.setName("战士");
        player.getProfession().add(Profession.warrior);
        player.setLevel(1);
        roleCaculate.initRole(player);
        log.debug("init play {}", JSONUtil.toJsonStr(player));
    }
}
