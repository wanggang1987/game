/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.timeline;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.SpringContextUtils;
import org.game.ms.lifecycle.AutoMonster;
import org.game.ms.lifecycle.AutoPlayer;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.skill.ResourceService;

/**
 *
 * @author wanggang
 */
@Slf4j
public class WheelBucket {

    private final AutoPlayer autoPlayer;
    private final AutoMonster autoMonster;
    private final LifeCycle lifeCycle;
    private final ResourceService skillService;

    public WheelBucket() {
        this.autoPlayer = SpringContextUtils.getBean(AutoPlayer.class);
        this.autoMonster = SpringContextUtils.getBean(AutoMonster.class);
        this.lifeCycle = SpringContextUtils.getBean(LifeCycle.class);
        this.skillService = SpringContextUtils.getBean(ResourceService.class);
    }

    private final List<TickTask> realTimeTasks = new ArrayList<>();

    public void addTaskToRealTime(TickTask task) {
        realTimeTasks.add(task);
    }

    public void work() {
        skillService.resourceUpdateForTick();
        autoPlayer.autoPlayForTick();
        autoMonster.autoMonsterForTick();
        lifeCycle.monsterDie();
        lifeCycle.playerDie();
    }
}
