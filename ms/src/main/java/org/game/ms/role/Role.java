/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import org.game.ms.fight.Battle;
import org.game.ms.map.Location;
import org.game.ms.map.RootMap;
import org.game.ms.skill.Skill;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {

    private RoleType roleType;
    private long id;
    private String name;
    private int level;
    private double speed;
    private double attackRange;
    private double healthPoint;
    private double healthMax;
    private double resourcePoint;
    private double resourceMax;
    private double attack;
    private double defense;
    private double attackPower;
    private double magicPower;

    private RootMap map;
    private Location location;

    private RoleType targetType;
    private Long targetId;
    private LivingStatus livingStatus;
    private MoveStatus moveStatus;
    private AttackStatus attackStatus;
    private Battle battle;

    private double attackCooldown;
    private double attackCooldownMax;
    private double skillColldown;
    private double skillCooldownMax;
    @JsonIgnore
    private List<Skill> skills;
}
