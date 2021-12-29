/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Buffers {

    private List<Buffer> buffers = new ArrayList<>();
    private List<Buffer> deBuffers = new ArrayList<>();
    private List<Buffer> anomalies = new ArrayList<>();
}
