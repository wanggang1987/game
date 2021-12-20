/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.game.ms.map.Location;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleMsg {

    private long id;
    private String name;
    private int level;
    private double speed;
    private double attackRange;
    private double healthPoint;
    private double healthMax;
    private Location location;

}
