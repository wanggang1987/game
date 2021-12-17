/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.timeline;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.game.ms.skill.buffer.Buffer;

/**
 *
 * @author wanggang
 */
@Data
@AllArgsConstructor
public class BufferManagerTask {

    private Buffer buffer;
    private boolean isAdd;
    private long ms;
}
