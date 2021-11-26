/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.timeline;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
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

    final private Integer ticksPerWheel = 10;
    final private Integer tickDuration = 100;
    final private WheelBucket[] wheel = new WheelBucket[ticksPerWheel];
    private ForkJoinPool threadPool;

    @PostConstruct
    private void init() {
        for (int i = 0; i < ticksPerWheel; i++) {
            wheel[i] = new WheelBucket();
        }
        threadPool = new ForkJoinPool(1);

        threadPool.submit(() -> {
            //start the thread
            while (true) {
                try {
                    cycleManager();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

        });
    }

    private void cycleManager() {
        long start = System.currentTimeMillis();
        long wheelStart = start / 1000 * 1000;
        long deadLine = (start / 1000 + 1) * 1000;
//        log.debug("cycleManager from {} to {},wheelStart {} ", start, deadLine, wheelStart);

        for (int tick = 0; tick < ticksPerWheel; tick++) {
            wheel[tick].work();
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
