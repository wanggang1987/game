/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import javax.annotation.PostConstruct;

import org.game.ms.client.msg.AttributeMsg;
import org.game.ms.client.msg.AttributeRequest;
import org.game.ms.client.msg.CastSkillMsg;
import org.game.ms.client.msg.CastSkillRequest;
import org.game.ms.client.msg.FightDamageMsg;
import org.game.ms.client.msg.FightStatusMsg;
import org.game.ms.client.msg.LocationMsg;
import org.game.ms.client.msg.MessageType;
import org.game.ms.client.msg.RoleDieMsg;
import org.game.ms.client.msg.WsMessage;
import org.game.ms.func.FuncUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.map.GridService;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
import org.game.ms.role.Role;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author gangwang
 */
@Slf4j
@Service
public class BuildMessageService {

    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private GridService gridService;
    @Autowired
    private MessageService messageService;

    private boolean buildMessage = false;

    public void readyBuildMessage() {
        buildMessage = true;
    }

    private void buildRoleMove() {
        while (buildMessage) {
            Role role = messageService.getRoleMove().poll();
            if (FuncUtils.isEmpty(role)) {
                break;
            }
            List<Player> receivers = gridService.playersInNearGrids(role.getLocation());
            for (Player revceiver : receivers) {
                if (!messageService.getPlayerSession().containsKey(revceiver.getId())) {
                    continue;
                }
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.LOCATION);
                message.setSeesionId(messageService.getPlayerSession().get(revceiver.getId()));
                LocationMsg locaionMsg = new LocationMsg();
                FuncUtils.copyProperties(role, locaionMsg);
                FuncUtils.copyProperties(role.getLocation(), locaionMsg);
                locaionMsg.setUpdateTime(FuncUtils.currentTime().getTime());
                message.setLocationMsg(locaionMsg);
                messageService.getSendQueue().offer(message);
            }
        }
    }

    private void buildFlushGrid() {
        while (buildMessage) {
            Role reveiver = messageService.getFlashGrid().poll();
            if (FuncUtils.isEmpty(reveiver)) {
                break;
            }
            if (!messageService.getPlayerSession().containsKey(reveiver.getId())) {
                continue;
            }
            List<Player> gridPlayers = gridService.playersInNearGrids(reveiver.getLocation());
            for (Player player : gridPlayers) {
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.LOCATION);
                message.setSeesionId(messageService.getPlayerSession().get(reveiver.getId()));
                LocationMsg locaionMsg = new LocationMsg();
                FuncUtils.copyProperties(player, locaionMsg);
                FuncUtils.copyProperties(player.getLocation(), locaionMsg);
                locaionMsg.setUpdateTime(FuncUtils.currentTime().getTime());
                message.setLocationMsg(locaionMsg);
                messageService.getSendQueue().offer(message);
            }
            List<Monster> gridMonsters = gridService.monstersInNearGrid(reveiver.getLocation());
            for (Monster monster : gridMonsters) {
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.LOCATION);
                message.setSeesionId(messageService.getPlayerSession().get(reveiver.getId()));
                LocationMsg locaionMsg = new LocationMsg();
                FuncUtils.copyProperties(monster, locaionMsg);
                FuncUtils.copyProperties(monster.getLocation(), locaionMsg);
                locaionMsg.setUpdateTime(FuncUtils.currentTime().getTime());
                message.setLocationMsg(locaionMsg);
                messageService.getSendQueue().offer(message);
            }
        }
    }

    private void buildRoleAttribute() {
        while (buildMessage) {
            WsMessage message = messageService.getRoleAttribute().poll();
            if (FuncUtils.isEmpty(message)) {
                return;
            }
            AttributeRequest request = message.getAttributeRequest();
            Role role = null;
            if (FuncUtils.equals(request.getRoleType(), RoleType.MONSTER)) {
                role = lifeCycle.onlineMonster(request.getRoleId());
            } else if (FuncUtils.equals(request.getRoleType(), RoleType.PLAYER)) {
                role = lifeCycle.onlinePlayer(request.getRoleId());
            }
            if (FuncUtils.isEmpty(role)) {
                continue;
            }
            message.setMessageType(MessageType.ATTRIBUTE);
            AttributeMsg attributeMsg = new AttributeMsg();
            FuncUtils.copyProperties(role, attributeMsg);
            attributeMsg.setUpdateTime(FuncUtils.currentTime().getTime());
            message.setAttributeMsg(attributeMsg);
            messageService.getSendQueue().offer(message);
        }
    }

    private void buildRoleDie() {
        while (buildMessage) {
            Role role = messageService.getRoleDie().poll();
            if (FuncUtils.isEmpty(role)) {
                return;
            }
            List<Player> receivers = gridService.playersInNearGrids(role.getLocation());
            for (Player revceiver : receivers) {
                if (!messageService.getPlayerSession().containsKey(revceiver.getId())) {
                    continue;
                }
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.ROLE_DIE);
                message.setSeesionId(messageService.getPlayerSession().get(revceiver.getId()));
                RoleDieMsg roleDieMsg = new RoleDieMsg();
                FuncUtils.copyProperties(role, roleDieMsg);
                message.setRoleDieMsg(roleDieMsg);
                messageService.getSendQueue().offer(message);
            }
        }
    }

    private void buildHeroUpdate() {
        while (buildMessage) {
            Player player = messageService.getHeroUpdate().poll();
            if (FuncUtils.isEmpty(player)) {
                return;
            }
            if (!messageService.getPlayerSession().containsKey(player.getId())) {
                return;
            }
            WsMessage message = new WsMessage();
            message.setMessageType(MessageType.HERO_UPDATE);
            message.setSeesionId(messageService.getPlayerSession().get(player.getId()));
            AttributeMsg attributeMsg = new AttributeMsg();
            FuncUtils.copyProperties(player, attributeMsg);
            attributeMsg.setUpdateTime(FuncUtils.currentTime().getTime());
            message.setAttributeMsg(attributeMsg);
            FightStatusMsg fightStatusMsg = new FightStatusMsg();
            FuncUtils.copyProperties(player, fightStatusMsg);
            fightStatusMsg.setUpdateTime(FuncUtils.currentTime().getTime());
            message.setFightStatusMsg(fightStatusMsg);
            messageService.getSendQueue().offer(message);
        }
    }

    private void buildFightStatus() {
        while (buildMessage) {
            Role role = messageService.getFightStatus().poll();
            if (FuncUtils.isEmpty(role)) {
                break;
            }
            List<Player> receivers = gridService.playersInNearGrids(role.getLocation());
            for (Player revceiver : receivers) {
                if (!messageService.getPlayerSession().containsKey(revceiver.getId())) {
                    continue;
                }
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.FIGHTSTATUS);
                message.setSeesionId(messageService.getPlayerSession().get(revceiver.getId()));
                FightStatusMsg fightStatusMsg = new FightStatusMsg();
                FuncUtils.copyProperties(role, fightStatusMsg);
                fightStatusMsg.setUpdateTime(FuncUtils.currentTime().getTime());
                message.setFightStatusMsg(fightStatusMsg);
                messageService.getSendQueue().offer(message);
            }
        }
    }

    private void buildCastSkill() {
        while (buildMessage) {
            CastSkillRequest request = messageService.getCastSkill().poll();
            if (FuncUtils.isEmpty(request)) {
                break;
            }
            CastSkillMsg castSkillMsg = new CastSkillMsg();
            castSkillMsg.setSourceId(request.getSource().getId());
            castSkillMsg.setSourceType(request.getSource().getRoleType());
            castSkillMsg.setTargetId(request.getTarget().getId());
            castSkillMsg.setTargetType(request.getTarget().getRoleType());
            castSkillMsg.setSkillId(request.getSkill().getId());
            castSkillMsg.setSkillType(request.getSkill().getSkillType());
            castSkillMsg.setSkillName(request.getSkill().getName());
            castSkillMsg.setTargetX(request.getTarget().getLocation().getX());
            castSkillMsg.setTargetY(request.getTarget().getLocation().getY());

            List<Player> receivers = gridService.playersInNearGrids(request.getSource().getLocation());
            for (Player revceiver : receivers) {
                if (!messageService.getPlayerSession().containsKey(revceiver.getId())) {
                    continue;
                }

                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.CASTSKILL);
                message.setSeesionId(messageService.getPlayerSession().get(revceiver.getId()));
                message.setCastSkillMsg(castSkillMsg);
                messageService.getSendQueue().offer(message);
            }
        }
    }

    private void buildFightDamage() {
        while (buildMessage) {
            FightDamageMsg fightDamageMsg = messageService.getFightDamage().poll();
            if (FuncUtils.isEmpty(fightDamageMsg)) {
                break;
            }
            if (FuncUtils.equals(fightDamageMsg.getSourceType(), RoleType.PLAYER)
                    && messageService.getPlayerSession().containsKey(fightDamageMsg.getSourceId())) {
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.FIGHTDAMAGE);
                message.setSeesionId(messageService.getPlayerSession().get(fightDamageMsg.getSourceId()));
                message.setFightDamageMsg(fightDamageMsg);
                messageService.getSendQueue().offer(message);
            } else if (FuncUtils.equals(fightDamageMsg.getTargetType(), RoleType.PLAYER)
                    && messageService.getPlayerSession().containsKey(fightDamageMsg.getTargetId())) {
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.FIGHTDAMAGE);
                message.setSeesionId(messageService.getPlayerSession().get(fightDamageMsg.getTargetId()));
                message.setFightDamageMsg(fightDamageMsg);
                messageService.getSendQueue().offer(message);
            }
        }
    }

    private void buildMessages() throws InterruptedException {
        Thread.sleep(1);
        if (!buildMessage) {
            return;
        }
        buildHeroUpdate();
        buildRoleAttribute();
        buildCastSkill();
        buildFightDamage();
        buildFightStatus();
        buildRoleMove();
        buildFlushGrid();
        buildRoleDie();
        buildMessage = false;
    }

    @PostConstruct
    private void startThread() throws InterruptedException {
        ForkJoinPool threadPool = new ForkJoinPool(1);
        threadPool.submit(() -> {
            while (true) {
                try {
                    buildMessages();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
