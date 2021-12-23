/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client;

import java.util.List;
import org.game.ms.client.msg.AttributeMsg;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gangwang
 */
@Service
public class BuildMessageService {

    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private GridService gridService;
    @Autowired
    private ClientService clientService;

    private boolean buildMessage = false;

    public void readyBuildMessage() {
        buildMessage = true;
    }

    private void buildPlayerUpdate() {
        while (buildMessage) {
            Long playerId = clientService.getPlayerUpdate().poll();
            if (FuncUtils.isEmpty(playerId)) {
                break;
            }
            if (!clientService.getPlayerSession().containsKey(playerId)) {
                continue;
            }
            WsMessage message = new WsMessage();
            message.setMessageType(MessageType.HERO_ATTRIBUTE);
            message.setSeesionId(clientService.getPlayerSession().get(playerId));
            Player player = lifeCycle.onlinePlayer(playerId);
            AttributeMsg attribute = new AttributeMsg();
            FuncUtils.copyProperties(player, attribute);
            message.setAttributeMsg(attribute);
            clientService.getSendQueue().offer(message);
        }
    }

    private void buildPlayerMove() {
        while (buildMessage) {
            Role player = clientService.getPlayerMove().poll();
            if (FuncUtils.isEmpty(player)) {
                break;
            }
            if (!clientService.getPlayerSession().containsKey(player.getId())) {
                continue;
            }
            WsMessage message = new WsMessage();
            message.setMessageType(MessageType.HERO_LOCATION);
            message.setSeesionId(clientService.getPlayerSession().get(player.getId()));
            LocationMsg locaionMsg = new LocationMsg();
            FuncUtils.copyProperties(player.getLocation(), locaionMsg);
            message.setLocationMsg(locaionMsg);
            clientService.getSendQueue().offer(message);
        }
    }

    private void buildMonsterMove() {
        while (buildMessage) {
            Role monster = clientService.getMonsterMove().poll();
            if (FuncUtils.isEmpty(monster)) {
                break;
            }
            List<Long> gridPlayerIds = gridService.playerIdsInGrid(monster.getLocation().getGrid());
            for (Long playerId : gridPlayerIds) {
                if (!clientService.getPlayerSession().containsKey(playerId)) {
                    continue;
                }
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.MONSTER_LOCATION);
                message.setSeesionId(clientService.getPlayerSession().get(playerId));
                LocationMsg locaionMsg = new LocationMsg();
                locaionMsg.setId(monster.getId());
                FuncUtils.copyProperties(monster.getLocation(), locaionMsg);
                message.setLocationMsg(locaionMsg);
                clientService.getSendQueue().offer(message);
            }
        }
    }

    private void buildPlayerGrid() {
        while (buildMessage) {
            Role player = clientService.getFlashGrid().poll();
            if (FuncUtils.isEmpty(player)) {
                break;
            }
            if (!clientService.getPlayerSession().containsKey(player.getId())) {
                continue;
            }
            List<Long> gridMonsterIds = gridService.monsterIdsInGrid(player.getLocation().getGrid());
            for (Long monsterId : gridMonsterIds) {
                Monster monster = lifeCycle.onlineMonster(monsterId);
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.MONSTER_LOCATION);
                message.setSeesionId(clientService.getPlayerSession().get(player.getId()));
                LocationMsg locaionMsg = new LocationMsg();
                locaionMsg.setId(monster.getId());
                FuncUtils.copyProperties(monster.getLocation(), locaionMsg);
                message.setLocationMsg(locaionMsg);
                clientService.getSendQueue().offer(message);
            }
        }
    }

    private void buildMonsterDie() {
        while (buildMessage) {
            Role monster = clientService.getMonsterDie().poll();
            if (FuncUtils.isEmpty(monster)) {
                return;
            }
            List<Long> gridPlayerIds = gridService.playerIdsInGrid(monster.getLocation().getGrid());
            for (Long playerId : gridPlayerIds) {
                if (!clientService.getPlayerSession().containsKey(playerId)) {
                    continue;
                }
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.MONSTER_DIE);
                message.setSeesionId(clientService.getPlayerSession().get(playerId));
                RoleDieMsg roleDieMsg = new RoleDieMsg();
                roleDieMsg.setId(monster.getId());
                message.setRoleDieMsg(roleDieMsg);
                clientService.getSendQueue().offer(message);
            }
        }
    }

    public void buildMessages() throws InterruptedException {
        Thread.sleep(1);
        if (!buildMessage) {
            return;
        }
        buildPlayerUpdate();
        buildPlayerMove();
        buildMonsterMove();
        buildPlayerGrid();
        buildMonsterDie();
        buildMessage = false;
    }
}
