/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.timeline;

import java.util.concurrent.ForkJoinPool;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class TimeWheel {

    final private int ticksPerWheel = 10;
    final private int tickDuration = 100;
    final private WheelBucket[] wheels = new WheelBucket[ticksPerWheel];
    private ForkJoinPool threadPool;
    private int tick = 0;

    public int getTicksPerWheel(){
        return ticksPerWheel;
    }
    
    public void addTaskNextTick(TickTask task) {
        int nextTick = tick == ticksPerWheel ? 0 : tick + 1;
        wheels[nextTick].addTaskToRealTime(task);
    }

    @PostConstruct
    private void init() {
        for (int i = 0; i < ticksPerWheel; i++) {
            wheels[i] = new WheelBucket();
        }
        threadPool = new ForkJoinPool(1);

        threadPool.submit(() -> {
            //start the thread
            while (true) {
                try {
                    cycleManager();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void cycleManager() {
        long start = System.currentTimeMillis();
        long wheelStart = start / 1000 * 1000;
        long deadLine = (start / 1000 + 1) * 1000;
//        log.debug("cycleManager from {} to {},wheelStart {} ", start, deadLine, wheelStart);

        for (tick = 0; tick < ticksPerWheel; tick++) {
            wheels[tick].work();
            long now = System.currentTimeMillis();
            long target = wheelStart + ((tick + 1) * tickDuration);
            long sleepTimeMs = target - now;
            if (sleepTimeMs > 0) {
                try {
                    Thread.sleep(sleepTimeMs);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

}
