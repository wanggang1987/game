package org.game.ms.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BufferEffect {

    private EffectStatus effectStatus;
    private int lastTime;
    private int persent;
}
