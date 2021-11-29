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
import org.game.ms.lifecycle.AutoPlay;

/**
 *
 * @author wanggang
 */
@Slf4j
public class WheelBucket {

    private AutoPlay autoPlay;

    public WheelBucket() {
        this.autoPlay = SpringContextUtils.getBean(AutoPlay.class);
    }

    private final List<TickTask> realTimeTasks = new ArrayList<>();

    public void addTaskToRealTime(TickTask task) {
        realTimeTasks.add(task);
    }

    private void autoPlaytask() {
        autoPlay.autoPlayForTick();
    }

    public void work() {
        autoPlaytask();
    }
}
