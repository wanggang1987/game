/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client;

import java.util.concurrent.ForkJoinPool;
import javax.annotation.PostConstruct;
import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.msg.AttributeRequest;
import org.game.ms.client.msg.CreatePlayerMsg;
import org.game.ms.client.msg.MessageType;
import org.game.ms.client.msg.WsMessage;
import org.game.ms.func.FuncUtils;
import org.game.ms.func.JsonUtils;
import org.game.ms.lifecycle.AutoPlayer;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.map.WorldMap;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gangwang
 */
@Slf4j
@Service
public class ProcessMessageService {

    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private AutoPlayer autoPlay;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private WebsocketController websocket;

    private void sendMessages() throws InterruptedException {
        WsMessage message = messageService.getSendQueue().peek();
        if (FuncUtils.isEmpty(message)) {
            Thread.sleep(1);
            return;
        }
        message = messageService.getSendQueue().poll();
        websocket.sendMessage(message);
    }

    private void receiveMessages() throws InterruptedException {
        WsMessage message = messageService.getReceiveQueue().peek();
        if (FuncUtils.isEmpty(message)) {
            Thread.sleep(1);
            return;
        }
        message = messageService.getReceiveQueue().poll();
        processMessage(message);
    }

    private void processMessage(WsMessage wsMessage) {
        if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.PLAYER_CREATE)) {
            Player player = createPlayer(wsMessage.getCreatePlayerMsg());
            messageService.addPlayerSession(player.getId(), wsMessage.getSeesionId());
            messageService.heroUpdate(player.getId());
        } else if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.LOGIN)) {
            messageService.addPlayerSession(wsMessage.getPlayerId(), wsMessage.getSeesionId());
        }
    }

    private void sendRoleAttribute(String sessionId, long roleIdL, RoleType roleType) {
        messageService.getRoleAttribute().add(new AttributeRequest(sessionId, roleIdL, roleType));
    }

    private Player createPlayer(CreatePlayerMsg msg) {
        Player player = playerService.createPlayer(msg.getName());
        lifeCycle.playerOnline(player);
        autoPlay.startPlayerAutoPlay(player);
        log.debug("createPlayer{}", JsonUtils.bean2json(player));
        return player;
    }

    @PostConstruct
    private void startThread() throws InterruptedException {
        ForkJoinPool threadPool = new ForkJoinPool(2);
        threadPool.submit(() -> {
            while (true) {
                try {
                    sendMessages();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        threadPool.submit(() -> {
            while (true) {
                try {
                    receiveMessages();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
