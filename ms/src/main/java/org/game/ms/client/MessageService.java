/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.websocket.Session;
import lombok.Data;
import org.game.ms.client.msg.WsMessage;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.msg.CastSkillMsg;
import org.game.ms.client.msg.FightDamageMsg;
import org.game.ms.func.FuncUtils;
import org.game.ms.player.Player;
import org.game.ms.role.Role;
import org.game.ms.role.RoleType;
import org.game.ms.skill.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Data
@Slf4j
@Service
public class MessageService {

    @Autowired
    private BuildMessageService buildMessageService;

    private final BiMap<Long, String> playerSession = HashBiMap.create();
    private final Queue<WsMessage> receiveQueue = new ConcurrentLinkedQueue<>();
    private final Queue<WsMessage> sendQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Role> roleMove = new ConcurrentLinkedQueue<>();
    private final Queue<Role> flashGrid = new ConcurrentLinkedQueue<>();
    private final Queue<Role> roleDie = new ConcurrentLinkedDeque<>();
    private final Queue<WsMessage> roleAttribute = new ConcurrentLinkedDeque<>();
    private final Queue<Player> heroUpdate = new ConcurrentLinkedDeque<>();
    private final Queue<FightDamageMsg> fightDamage = new ConcurrentLinkedDeque<>();
    private final Queue<CastSkillMsg> castSkill = new ConcurrentLinkedDeque<>();
    private final Queue<Role> fightStatus = new ConcurrentLinkedDeque<>();

    public void addCastSkill(Role role, Skill skill) {
        CastSkillMsg castSkillMsg = new CastSkillMsg();
        castSkillMsg.setSourceId(role.getId());
        castSkillMsg.setSourceType(role.getRoleType());
        castSkillMsg.setTargetId(role.getTarget().getId());
        castSkillMsg.setTargetType(role.getTarget().getRoleType());
        castSkillMsg.setSkillId(skill.getId());
        castSkillMsg.setSkillType(skill.getSkillType());
        castSkillMsg.setSkillName(skill.getName());
        castSkillMsg.setGrid(role.getLocation().getGrid());
        castSkillMsg.setTargetX(role.getTarget().getLocation().getX());
        castSkillMsg.setTargetY(role.getTarget().getLocation().getY());
        castSkill.add(castSkillMsg);
    }

    public void addFightDamage(Role source, double damage, Skill skill, Role target) {
        FightDamageMsg fightDamageMsg = new FightDamageMsg();
        fightDamageMsg.setSourceId(source.getId());
        fightDamageMsg.setSourceType(source.getRoleType());
        fightDamageMsg.setTargetId(target.getId());
        fightDamageMsg.setTargetType(target.getRoleType());
        fightDamageMsg.setSkillId(skill.getId());
        fightDamageMsg.setSkillName(skill.getName());
        fightDamageMsg.setDamage(damage);
        fightDamage.add(fightDamageMsg);
    }

    public void heroUpdate(Player player) {
        heroUpdate.add(player);
    }

    public void addPlayerSession(Long playerId, String sessionId) {
        if (playerSession.containsKey(playerId)) {
            return;
        }
        playerSession.put(playerId, sessionId);
    }

    public void removeSession(Session session) {
        playerSession.inverse().remove(session.getId());
    }

    public void readyBuildMessage() {
        buildMessageService.readyBuildMessage();
    }

    public void addRoleDieMsg(Role role) {
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
        }
        roleDie.add(role);
    }

    public void addRoleMoveMsg(Role role) {
        roleMove.add(role);
    }

    public void addRoleToGridMsg(Role role) {
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            flashGrid.add(role);
            roleMove.add(role);
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            roleMove.add(role);
        }
    }

}
