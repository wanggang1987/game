/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final Map<Profession, List<Skill>> professionSkill = new HashMap<>();
    private final Map<Long, Skill> idSkillMap = new HashMap<>();

    @PostConstruct
    private void initSkillTemplate() {
        skillTemplateCollection.getSkills().forEach(temmplate -> {
            List<Skill> professionSkills = professionSkill.get(temmplate.getProfession());
            if (professionSkills == null) {
                professionSkills = new ArrayList<>();
                professionSkill.put(temmplate.getProfession(), professionSkills);
            }
            professionSkills.add(temmplate);
            idSkillMap.put(temmplate.getId(), temmplate);
        });

        professionSkill.get(Profession.BASIC).forEach(skill -> {
            if (FuncUtils.equals(skill.getId(), 1110010501000000L)) {
                physicalAttack = skill;
            }
        });

        professionSkill.forEach((profession, skillList) -> {
            String str = skillList.stream().map(skill -> skill.getName()).collect(Collectors.joining(","));
            log.info("initSkillTemplate {}: {}", profession, str);
            skillList.forEach(skill -> log.info("{}", skill));
        });
    }

    public List<Skill> professionSkill(Profession profession) {
        return professionSkill.get(profession);
    }

    public Skill physicalAttack() {
        Skill skill = new Skill();
        FuncUtils.copyProperties(physicalAttack, skill);
        return skill;
    }

    public Skill getSkillById(long id) {
        Skill skill = new Skill();
        FuncUtils.copyProperties(idSkillMap.get(id), skill);
        return skill;
    }
}
