/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.game.ms.map.Location;
import org.game.ms.map.RootMap;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {

    private long id;
    private String name;
    private int level;
    private double speed;
    private double healthPoint;
    private double resourcePoint;
    private double attack;
    private double defense;
    private RootMap map;
    private Location location;
    private transient Role target;
    private MoveStatus moveStatus;
}
