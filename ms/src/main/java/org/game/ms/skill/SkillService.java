/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.player.Profession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class SkillService {

    @Autowired
    private SkillTemplateCollection skillTemplateCollection;
    private Skill physicalAttack;

    @PostConstruct
    private void initSkillTemplate() {
        skillTemplateCollection.setProfessionSkill(new HashMap<>());
        skillTemplateCollection.getSkills().forEach(temmplate -> {
            List<Skill> professionSkills = skillTemplateCollection.getProfessionSkill().get(temmplate.getProfession());
            if (professionSkills == null) {
                professionSkills = new ArrayList<>();
                skillTemplateCollection.getProfessionSkill().put(temmplate.getProfession(), professionSkills);
            }
            professionSkills.add(temmplate);
        });

        skillTemplateCollection.getProfessionSkill().get(Profession.BASIC).forEach(skill -> {
            if (FuncUtils.equals(skill.getId(), 1110000001000000L)) {
                physicalAttack = skill;
            }
        });

        skillTemplateCollection.getProfessionSkill().forEach((profession, skillList) -> {
            String str = skillList.stream().map(skill -> skill.getName()).collect(Collectors.joining(","));
            log.info("initSkillTemplate {}: {}", profession, str);
            skillList.forEach(skill -> log.info("{}", skill));
        });
    }

    public List<Skill> professionSkill(Profession profession) {
        return skillTemplateCollection.getProfessionSkill().get(profession);
    }

    public Skill physicalAttack() {
        return physicalAttack;
    }

}
