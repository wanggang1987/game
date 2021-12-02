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
import org.game.ms.lifecycle.AutoPlayer;
import org.game.ms.lifecycle.LifeCycle;

/**
 *
 * @author wanggang
 */
@Slf4j
public class WheelBucket {

    private AutoPlayer autoPlay;
    private LifeCycle lifeCycle;

    public WheelBucket() {
        this.autoPlay = SpringContextUtils.getBean(AutoPlayer.class);
        this.lifeCycle = SpringContextUtils.getBean(LifeCycle.class);
    }

    private final List<TickTask> realTimeTasks = new ArrayList<>();

    public void addTaskToRealTime(TickTask task) {
        realTimeTasks.add(task);
    }

    private void autoPlaytask() {
        autoPlay.autoPlayForTick();
    }

    private void cooldownTimer() {
        lifeCycle.cooldownTimer();
    }

    public void work() {
        cooldownTimer();
        autoPlaytask();
    }
}
