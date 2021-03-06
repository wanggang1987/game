/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.timeline;

import java.util.concurrent.ForkJoinPool;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class TimeWheel {
    
    @Autowired
    private WheelConfig wheelConfig;
    @Autowired
    private TaskService taskService;
    
    private WheelBucket[] wheels;
    
    private void addTaskToMs(TickTask task) {
        Long n = task.getTick() % wheelConfig.getTicksPerWheel();
        wheels[n.intValue()].addTaskToRealTime(task);
    }
    
    private void queueManager() {
        taskService.tasks().forEach(task -> addTaskToMs(task));
        taskService.tasks().clear();
    }
    
    @PostConstruct
    private void init() {
        wheels = new WheelBucket[wheelConfig.getTicksPerWheel()];
        for (int i = 0; i < wheelConfig.getTicksPerWheel(); i++) {
            wheels[i] = new WheelBucket();
        }
        ForkJoinPool threadPool = new ForkJoinPool(1);
        
        threadPool.submit(() -> {
            //start the thread
            while (true) {
                try {
                    cycleManager();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void cycleManager() throws InterruptedException {
        long start = System.currentTimeMillis();
        long wheelStart = start / 1000 * 1000;
        long deadLine = (start / 1000 + 1) * 1000;
//        log.debug("cycleManager from {} to {},wheelStart {} ", start, deadLine, wheelStart);
        for (int tick = 0; tick < wheelConfig.getTicksPerWheel(); tick++) {
            queueManager();
            wheels[tick].work();
            long now = System.currentTimeMillis();
            long target = wheelStart + ((tick + 1) * wheelConfig.getTickDuration());
            long sleepTimeMs = target - now;
            if (sleepTimeMs > 0) {
                Thread.sleep(sleepTimeMs);
            }
            wheelConfig.ticktock();
        }
    }
    
}
