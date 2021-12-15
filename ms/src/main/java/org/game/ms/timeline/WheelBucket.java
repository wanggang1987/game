/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.fight.FightService;
import org.game.ms.func.SpringContextUtils;
import org.game.ms.lifecycle.AutoMonster;
import org.game.ms.lifecycle.AutoPlayer;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.skill.resource.ResourceService;

/**
 *
 * @author wanggang
 */
@Slf4j
public class WheelBucket {

    private final WheelConfig wheelConfig;
    private final AutoPlayer autoPlayer;
    private final AutoMonster autoMonster;
    private final LifeCycle lifeCycle;
    private final ResourceService skillService;
    private final FightService fightService;

    public WheelBucket() {
        this.wheelConfig = SpringContextUtils.getBean(WheelConfig.class);
        this.autoPlayer = SpringContextUtils.getBean(AutoPlayer.class);
        this.autoMonster = SpringContextUtils.getBean(AutoMonster.class);
        this.lifeCycle = SpringContextUtils.getBean(LifeCycle.class);
        this.skillService = SpringContextUtils.getBean(ResourceService.class);
        this.fightService = SpringContextUtils.getBean(FightService.class);
    }

    private final List<TickTask> realTimeTasks = new ArrayList<>();

    public void addTaskToRealTime(TickTask task) {
        realTimeTasks.add(task);
        log.debug("addTaskToRealTime {} ", task);
    }

    private void excuteWheel() {
        List<TickTask> revmoeList = realTimeTasks.stream()
                .filter(task -> task.getTick() <= wheelConfig.getTick())
                .collect(Collectors.toList());
        revmoeList.forEach(task -> {
            fightService.damageTarget(task.getSource(), task.getDamage(), task.getSkill(), task.getTarget());
        });
        realTimeTasks.removeAll(revmoeList);
    }

    public void work() {
        excuteWheel();
        skillService.resourceUpdateForTick();
        autoPlayer.autoPlayForTick();
        autoMonster.autoMonsterForTick();
        lifeCycle.monsterDie();
        lifeCycle.playerDie();
    }
}
