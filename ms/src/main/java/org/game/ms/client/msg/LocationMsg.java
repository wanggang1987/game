/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client.msg;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.game.ms.role.RoleType;

import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationMsg {

    private long updateTime;
    private long id;
    private RoleType roleType;
    private double x;
    private double y;
    private String grid;
}
