/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.monster;

import lombok.Data;

/**
 *
 * @author gangwang
 */
@Data
public class MonsterTemplate  {

    private String name;
    private int level;
    private double speed;
    private double attackRange;
    private double attackCooldown;
    private double health;
    private double resource;
    private double attack;
    private double deffence;

}
