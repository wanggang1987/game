/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
import org.game.ms.role.LivingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class AutoMonster {

    @Autowired
    private LifeCycle lifeCycle;

    private void playerAuto(Monster monster) {
        if (monster.getTarget() == null) {
            return;
        }
        if (LivingStatus.DEAD.equals(player.getTarget().getLivingStatus())) {
            player.setTarget(null);
            return;
        }
        
    }

    public void autoMonsterForTick() {
        lifeCycle.onlineMonsters().forEach(monster -> playerAuto(monster));
    }
}
