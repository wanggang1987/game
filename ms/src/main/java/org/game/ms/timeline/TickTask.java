/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.timeline;

import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
public class TickTask {

    private BufferManagerTask bufferManagerTask;
    private LoopDamageTask loopDamageTask;
    private long tick;
    private long ms;

}
