/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.monster;

import cn.hutool.core.bean.BeanUtil;
import org.game.ms.monster.template.WolfTemplate;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
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
        monster.setId(id++);
        monster.setAttackStatus(AttackStatus.NOT_ATTACK);
        monster.setMoveStatus(MoveStatus.STANDING);
        monster.setLivingStatus(LivingStatus.LIVING);
        BeanUtil.copyProperties(wolfTemplate, monster);
        return monster;
    }
}
