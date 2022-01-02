/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.monster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.id.IdService;
import org.game.ms.skill.resource.Resource;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.Attribute;
import org.game.ms.role.RoleType;
import org.game.ms.skill.SkillService;
import org.game.ms.skill.buffer.BufferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class MonsterService {
    
    @Autowired
    private IdService idService;
    @Autowired
    private MonsterTemplateCollection monsterTemplateList;
    @Autowired
    private SkillService skillService;
    @Autowired
    private BufferService bufferService;
    
    @PostConstruct
    private void initMonsterTemplate() {
        monsterTemplateList.setWorldLevel(new HashMap<>());
        monsterTemplateList.getMonsters().forEach(template -> {
            List<MonsterTemplate> levelTemples = monsterTemplateList.getWorldLevel().get(template.getLevel());
            if (levelTemples == null) {
                levelTemples = new ArrayList<>();
                monsterTemplateList.getWorldLevel().put(template.getLevel(), levelTemples);
            }
            levelTemples.add(template);
        });
    }
    
    public Monster initMonsterByLevel(int level) {
        MonsterTemplate template = findTemplateByLevel(level);
        Monster monster = new Monster();
        monster.setTemplate(template);
        monster.setRoleType(RoleType.MONSTER);
        monster.setId(idService.newId());
        monster.setName(template.getName());
        monster.setLevel(template.getLevel());
        monster.setBaseSpeed(template.getSpeed() / 1000);
        monster.setFinalSpeed(monster.getBaseSpeed());
        monster.setAttackRange(template.getAttackRange());
        
        Attribute roleAttribute = monster.getAttribute();
        monster.setHealthMax(template.getHealth());
        monster.setHealthPoint(monster.getHealthMax());
        roleAttribute.setAttackPower(template.getAttackPower());
        
        monster.setAttackStatus(AttackStatus.NOT_ATTACK);
        monster.setMoveStatus(MoveStatus.STANDING);
        monster.setLivingStatus(LivingStatus.LIVING);
        monster.setNormalAttack(skillService.physicalAttack());
        Resource resource = monster.getResource();
        resource.setAttackCooldownMax(template.getAttackCooldown() * 1000);
        resource.setSkillCooldownMax(1.5 * 1000);
        resource.setAngerMax(100);
        resource.setAngerPoint(resource.getAngerMax());
        return monster;
    }
    
    public void initMonster(Monster monster) {
        bufferService.clear(monster.getBuffers());
    }
    
    private MonsterTemplate findTemplateByLevel(int level) {
        List<MonsterTemplate> levelTemples = monsterTemplateList.getWorldLevel().get(level);
        if (levelTemples == null) {
            return findTemplateByLevel(level - 1);
        }
        int index = FuncUtils.randomZeroToRange(levelTemples.size());
        return levelTemples.get(index);
    }
    
}
