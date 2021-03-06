/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.game.ms.fight.Battle;
import org.game.ms.map.Location;
import org.game.ms.map.RootMap;
import org.game.ms.skill.resource.Resource;
import org.game.ms.skill.Skill;
import org.game.ms.skill.buffer.Buffers;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {

    //attribut
    private long id;
    private RoleType roleType;
    private String name;
    private int level;
    private double baseSpeed;
    private double finalSpeed;
    private double attackRange;
    private Attribute attribute = new Attribute();

    //target
    @JsonIgnore
    private Role target;
    private double targetDistance;

    //status
    private double healthPoint;
    private double healthMax;
    private LivingStatus livingStatus;
    private MoveStatus moveStatus;
    private AttackStatus attackStatus;
    private Battle battle;
    private Resource resource = new Resource();
    private Buffers buffers = new Buffers();

    //skill
    private Skill normalAttack;
    private Skill counterAttack;
    private List<Skill> skills = new ArrayList<>();
    private RootMap map;
    private Location location;
}
