/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.mock;

import javax.annotation.PostConstruct;
import org.game.ms.role.Profession;
import org.game.ms.role.Role;
import org.game.ms.role.RoleCaculate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Component
public class Mock {

    @Autowired
    private RoleCaculate roleCaculate;
    
    @PostConstruct
    private void init() {

        Role play = new Role();
        play.getProfession().add(Profession.warrior);
        play.setLevel(1);
        roleCaculate.initRole(play);
    }
}
