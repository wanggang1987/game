/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.monster;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import org.game.ms.map.Location;

/**
 *
 * @author gangwang
 */
@Data
public class Monster {

    private MonsterTemplate template;

    private long id;
    private int level;
    private double speed;
    private double health;
    private double resource;
    private double attack;
    private double deffence;

    private Location location;

    public Monster(MonsterTemplate monsterTemplate) {
        this.template = monsterTemplate;
        BeanUtil.copyProperties(monsterTemplate, this);
    }

    public boolean canDestory() {
        return true;
    }
}
