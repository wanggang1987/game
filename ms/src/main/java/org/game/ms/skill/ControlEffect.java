package org.game.ms.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ControlEffect {

    private AnomalyStatus anomalyStatus;
    private int lastTime;
    private int persent;
}
