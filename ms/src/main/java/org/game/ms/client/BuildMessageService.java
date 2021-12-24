/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.msg.AttributeMsg;
import org.game.ms.client.msg.AttributeRequest;
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
            List<Long> reveiverIds = gridService.playerIdsInGrid(role.getLocation().getGrid());
            for (Long revcicerId : reveiverIds) {
                if (!messageService.getPlayerSession().containsKey(revcicerId)) {
                    continue;
                }
                WsMessage message = new WsMessage();
                if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
                    message.setMessageType(MessageType.MONSTER_LOCATION);
                } else if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
                    message.setMessageType(MessageType.PLAYER_LOCATION);
                }
                message.setSeesionId(messageService.getPlayerSession().get(revcicerId));
                LocationMsg locaionMsg = new LocationMsg();
                locaionMsg.setId(role.getId());
                FuncUtils.copyProperties(role.getLocation(), locaionMsg);
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
            List<Long> gridPlayerIds = gridService.playerIdsInGrid(reveiver.getLocation().getGrid());
            for (Long playerId : gridPlayerIds) {
                if (FuncUtils.equals(reveiver.getId(), playerId)) {
                    continue;
                }
                Player player = lifeCycle.onlinePlayer(playerId);
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.PLAYER_LOCATION);
                message.setSeesionId(messageService.getPlayerSession().get(reveiver.getId()));
                LocationMsg locaionMsg = new LocationMsg();
                locaionMsg.setId(player.getId());
                FuncUtils.copyProperties(player.getLocation(), locaionMsg);
                message.setLocationMsg(locaionMsg);
                messageService.getSendQueue().offer(message);
            }
            List<Long> gridMonsterIds = gridService.monsterIdsInGrid(reveiver.getLocation().getGrid());
            for (Long monsterId : gridMonsterIds) {
                Monster monster = lifeCycle.onlineMonster(monsterId);
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.MONSTER_LOCATION);
                message.setSeesionId(messageService.getPlayerSession().get(reveiver.getId()));
                LocationMsg locaionMsg = new LocationMsg();
                locaionMsg.setId(monster.getId());
                FuncUtils.copyProperties(monster.getLocation(), locaionMsg);
                message.setLocationMsg(locaionMsg);
                messageService.getSendQueue().offer(message);
            }
        }
    }

    private void buildRoleAttribute() {
        while (buildMessage) {
            AttributeRequest request = messageService.getRoleAttribute().poll();
            if (FuncUtils.isEmpty(request)) {
                return;
            }
            WsMessage message = new WsMessage();
            Role role = null;
            if (FuncUtils.equals(request.getRoleType(), RoleType.MONSTER)) {
                message.setMessageType(MessageType.MONSTER_ATTRIBUTE);
                role = lifeCycle.onlineMonster(request.getRoleId());
            } else if (FuncUtils.equals(request.getRoleType(), RoleType.PLAYER)) {
                message.setMessageType(MessageType.PLAYER_ATTRIBUTE);
                role = lifeCycle.onlinePlayer(request.getRoleId());
            }
            if (FuncUtils.isEmpty(role)) {
                continue;
            }
            AttributeMsg attributeMsg = new AttributeMsg();
            FuncUtils.copyProperties(role, attributeMsg);
            message.setSeesionId(request.getSessionId());
            message.setAttributeMsg(attributeMsg);
            messageService.getSendQueue().offer(message);
        }
    }

    private void buildMonsterDie() {
        while (buildMessage) {
            Role role = messageService.getRoleDie().poll();
            if (FuncUtils.isEmpty(role)) {
                return;
            }
            List<Long> reveiverIds = gridService.playerIdsInGrid(role.getLocation().getGrid());
            for (Long revcicerId : reveiverIds) {
                if (!messageService.getPlayerSession().containsKey(revcicerId)) {
                    continue;
                }
                WsMessage message = new WsMessage();
                if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
                    message.setMessageType(MessageType.MONSTER_DIE);
                } else if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
                    message.setMessageType(MessageType.PLAYER_DIE);
                }
                message.setSeesionId(messageService.getPlayerSession().get(revcicerId));
                RoleDieMsg roleDieMsg = new RoleDieMsg();
                roleDieMsg.setId(role.getId());
                message.setRoleDieMsg(roleDieMsg);
                messageService.getSendQueue().offer(message);
            }
        }
    }

    private void buildHeroUpdate() {
        while (buildMessage) {
            Long playerId = messageService.getHeroUpdate().poll();
            if (FuncUtils.isEmpty(playerId)) {
                return;
            }
            if (!messageService.getPlayerSession().containsKey(playerId)) {
                return;
            }
            WsMessage message = new WsMessage();
            message.setMessageType(MessageType.HERO_ATTRIBUTE);
            Player player = lifeCycle.onlinePlayer(playerId);
            AttributeMsg attributeMsg = new AttributeMsg();
            FuncUtils.copyProperties(player, attributeMsg);
            message.setSeesionId(messageService.getPlayerSession().get(playerId));
            message.setAttributeMsg(attributeMsg);
            messageService.getSendQueue().offer(message);
        }
    }

    private void buildMessages() throws InterruptedException {
        Thread.sleep(1);
        if (!buildMessage) {
            return;
        }
        buildHeroUpdate();
        buildRoleAttribute();
        buildRoleMove();
        buildFlushGrid();
        buildMonsterDie();
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
