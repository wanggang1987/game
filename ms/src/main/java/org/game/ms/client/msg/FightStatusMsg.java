/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author gangwang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FightStatusMsg {

    private long id;
    private double healthPoint;
    private double healthMax;
}
