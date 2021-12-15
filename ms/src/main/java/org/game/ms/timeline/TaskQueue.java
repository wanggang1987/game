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
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class TaskQueue {

    private final List<TickTask> queue = new ArrayList<>();

    public void addTask(TickTask task) {
        queue.add(task);
    }

    public Collection<TickTask> tasks() {
        return queue;
    }
}
