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
import org.game.ms.role.AttackStatus;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.RoleService;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class MonsterService extends RoleService {

    @Autowired
    private IdService idService;
    @Autowired
    private MonsterTemplateCollection monsterTemplateList;

    @PostConstruct
    private void initMonsterTemplate() {
        monsterTemplateList.setWorldLevel(new HashMap<>());
        monsterTemplateList.getWorldAll().forEach(template -> {
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
        monster.setRoleType(RoleType.MONSTER);
        monster.setId(idService.newId());
        monster.setName(template.getName());
        monster.setLevel(template.getLevel());
        monster.setSpeed(template.getSpeed() / 1000);
        monster.setAttackRange(template.getAttackRange());
        monster.setAttackCooldownMax(template.getAttackCooldown() * 1000);
        monster.setHealthMax(template.getHealth());
        monster.setHealthPoint(monster.getHealthMax());
        monster.setResourceMax(template.getResource());
        monster.setResourcePoint(monster.getResourceMax());
        monster.setAttack(template.getAttack());
        monster.setDefense(template.getDeffence());
        monster.setAttackStatus(AttackStatus.NOT_ATTACK);
        monster.setMoveStatus(MoveStatus.STANDING);
        monster.setLivingStatus(LivingStatus.LIVING);
        return monster;
    }

    private MonsterTemplate findTemplateByLevel(int level) {
        List<MonsterTemplate> levelTemples = monsterTemplateList.getWorldLevel().get(level);
        if (levelTemples == null) {
            return findTemplateByLevel(level - 1);
        }
        int index = FuncUtils.randomZeroToRange(levelTemples.size());
        return levelTemples.get(index);
    }

    public void removeFromMap(Monster monster) {
        monster.getMap().removeMonsterFromMap(monster);
    }
}
