/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.timeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class TaskService {

    @Autowired
    private WheelConfig wheelConfig;
    private final List<TickTask> queue = new ArrayList<>();

    public void addTask(LoopDamageTask loopDamageTask) {
        TickTask tickTask = new TickTask();
        tickTask.setLoopDamageTask(loopDamageTask);
        long ticks = wheelConfig.getTick() + loopDamageTask.getMs() / wheelConfig.getTickDuration();
        tickTask.setTick(ticks);
        queue.add(tickTask);
    }

    public void addTask(BufferManagerTask bufferManagerTask) {
        TickTask tickTask = new TickTask();
        tickTask.setBufferManagerTask(bufferManagerTask);
        long ticks = wheelConfig.getTick() + bufferManagerTask.getMs() / wheelConfig.getTickDuration();
        tickTask.setTick(ticks);
        queue.add(tickTask);
    }

    public Collection<TickTask> tasks() {
        return queue;
    }
}
