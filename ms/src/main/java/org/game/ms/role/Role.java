/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.game.ms.fight.Battle;
import org.game.ms.map.Location;
import org.game.ms.map.RootMap;
import org.game.ms.skill.resource.Resource;
import org.game.ms.skill.Skill;
import org.game.ms.skill.buffer.Buffer;

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
    private double attack;
    private double defense;
    private double attackPower;
    private double magicPower;

    private RootMap map;
    private Location location;

    private RoleType targetType;
    private Long targetId;
    private double targetDistance;
    private LivingStatus livingStatus;
    private MoveStatus moveStatus;
    private AttackStatus attackStatus;
    private Battle battle;
    private List<Skill> skills = new ArrayList<>();
    private Resource resource = new Resource();
    private List<Buffer> buffers = new ArrayList<>();
}
