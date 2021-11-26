/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import org.game.ms.role.template.WarriorTample;
import org.game.ms.role.template.RoleTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Slf4j
@Component
public class RoleCaculate {

    @Autowired
    private WarriorTample warriorTample;

    Map<Profession, RoleTemplate> mapProfessionRole = new HashMap<>();

    @PostConstruct
    private void init() {
        mapProfessionRole.put(Profession.warrior, warriorTample);
    }

    public void initRole(Role role) {
        role.getProfession().stream().forEach(profession -> {
            RoleTemplate template = mapProfessionRole.get(profession);
            role.setHealthPoint(role.getHealthPoint() + template.getBaseHealth() + template.getGrowthHealth() * role.getLevel());
            role.setResourcePoint(role.getResourcePoint()+ template.getBaseResource()+ template.getGrowthResource()* role.getLevel());
            role.setAttack(role.getAttack()+ template.getBaseAttack()+ template.getGrowthAttack()* role.getLevel());
            role.setDefense(role.getDefense()+ template.getBaseDeffence()+ template.getGrowthDefense()* role.getLevel());
        });
    }
}
