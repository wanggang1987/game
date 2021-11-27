/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
public class Role {

    private List<Profession> profession = new ArrayList<>();
    private String name;
    private int level = 0;
    private double healthPoint = 0;
    private double resourcePoint = 0;
    private double attack = 0;
    private double defense = 0;

}
