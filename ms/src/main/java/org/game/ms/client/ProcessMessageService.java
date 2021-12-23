/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client;

import lombok.extern.slf4j.Slf4j;
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
    private WorldMap worldMap;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private WebsocketController websocket;

    public void sendMessages() throws InterruptedException {
        WsMessage message = clientService.getSendQueue().peek();
        if (FuncUtils.isEmpty(message)) {
            Thread.sleep(1);
            return;
        }
        message = clientService.getSendQueue().poll();
        websocket.sendMessage(message);
    }

    public void receiveMessages() throws InterruptedException {
        WsMessage message = clientService.getReceiveQueue().peek();
        if (FuncUtils.isEmpty(message)) {
            Thread.sleep(1);
            return;
        }
        message = clientService.getReceiveQueue().poll();
        processMessage(message);
    }

    private void processMessage(WsMessage wsMessage) {
        if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.PLAYER_CREATE)) {
            Player player = createPlayer(wsMessage.getCreatePlayerMsg());
            clientService.addPlayerSession(player.getId(), wsMessage.getSeesionId());
            clientService.getPlayerUpdate().add(player.getId());
            clientService.getPlayerMove().add(player);
        } else if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.LOGIN)) {
            clientService.addPlayerSession(wsMessage.getPlayerId(), wsMessage.getSeesionId());
        }
    }

    private Player createPlayer(CreatePlayerMsg msg) {
        Player player = playerService.createPlayer(msg.getName());
        lifeCycle.playerOnline(player);
        playerService.playerGotoMap(player, worldMap);
        autoPlay.startPlayerAutoPlay(player);
        log.debug("createPlayer{}", JsonUtils.bean2json(player));
        return player;
    }

}
