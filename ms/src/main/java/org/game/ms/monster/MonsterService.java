/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.monster;

import org.game.ms.monster.template.WolfTemplate;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.FightingStatus;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Service
public class MonsterService {

    static int id = 0;

    @Autowired
    private WolfTemplate wolfTemplate;

    public Monster initMonster() {
        Monster monster = new Monster();
        monster.setRoleType(RoleType.MONSTER);
        monster.setId(id++);
        monster.setSpeed(wolfTemplate.getSpeed() / 1000);
        monster.setAttackRange(wolfTemplate.getAttackRange());
        monster.setAttackCooldownMax(wolfTemplate.getAttackCooldown() * 1000);
        monster.setHealthMax(wolfTemplate.getHealth());
        monster.setHealthPoint(monster.getHealthMax());
        monster.setResourceMax(wolfTemplate.getResource());
        monster.setResourcePoint(monster.getResourceMax());
        monster.setAttack(wolfTemplate.getAttack());
        monster.setDefense(wolfTemplate.getDeffence());
        monster.setAttackStatus(AttackStatus.NOT_ATTACK);
        monster.setMoveStatus(MoveStatus.STANDING);
        monster.setLivingStatus(LivingStatus.LIVING);
        monster.setFightingStatus(FightingStatus.NOT_FIGHTING);
        return monster;
    }
}
