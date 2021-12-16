/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.skill.buffer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.game.ms.role.RoleType;
import org.game.ms.skill.Skill;

/**
 *
 * @author wanggang
 */
@Data
@AllArgsConstructor
public class Buffer {

    private long id;
    private long sourceId;
    private RoleType sourceType;
    private long targetId;
    private RoleType targetType;
    private Skill skill;
    private boolean buffer;
}
