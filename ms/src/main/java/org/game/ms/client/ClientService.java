/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client;

import com.google.common.collect.BiMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import javax.annotation.PostConstruct;
import org.game.ms.client.msg.MessageType;
import org.game.ms.client.msg.CreatePlayerMsg;
import org.game.ms.client.msg.WsMessage;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.msg.RoleMsg;
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
 * @author wanggang
 */
@Slf4j
@Service
public class ClientService {

    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private AutoPlayer autoPlay;
    @Autowired
    private WorldMap worldMap;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private WebsocketController websocket;

    private final Stack<WsMessage> sendStack = new Stack<>();
    private boolean buildMessage = false;
    private Set<Long> playerUpdate = new HashSet<>();

    public void addPlayerMoveMsg(Long id, RoleType type) {
        if (FuncUtils.equals(type, RoleType.PLAYER)) {
            playerUpdate.add(id);
        }
    }

    private void processMessage(WsMessage wsMessage) {
        if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.PLAYER_CREATE)) {
            Long playerId = createPlayer(wsMessage.getCreatePlayerMsg());
            websocket.addPlayerSession(playerId, wsMessage.getSeesionId());
            playerUpdate.add(playerId);
        } else if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.PLAYER_LOGIN)) {
            websocket.addPlayerSession(wsMessage.getPlayerId(), wsMessage.getSeesionId());
        }

    }

    private Long createPlayer(CreatePlayerMsg msg) {
        Player player = playerService.createPlayer(msg.getName());
        lifeCycle.playerOnline(player);
        playerService.playerGotoMap(player, worldMap);
        autoPlay.startPlayerAutoPlay(player);
        log.debug("createPlayer{}", JsonUtils.bean2json(player));
        return player.getId();
    }

    public void readyBuildMessage() {
        buildMessage = true;
    }

    private void buildPlayerUpdate(BiMap<Long, String> playerSession) {
        Collection<Long> temp = playerUpdate;
        playerUpdate = new HashSet<>();
        temp.forEach(id -> {
            if (playerSession.containsKey(id)) {
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.PLAYER_ATTRIBUTE);
                message.setPlayerId(id);
                message.setSeesionId(playerSession.get(id));
                Player player = lifeCycle.onlinePlayer(id);
                RoleMsg roleMsg = new RoleMsg();
                FuncUtils.copyProperties(player, roleMsg);
                message.setPlayerMsg(roleMsg);
                sendStack.push(message);
            }
        });
    }

    private void buildMessages() throws InterruptedException {
        Thread.sleep(20);
        if (!buildMessage) {
            return;
        }
        buildMessage = false;
        if (playerUpdate.isEmpty()) {
            Thread.sleep(1);
            return;
        }
        BiMap<Long, String> playerSession = websocket.playerSession();
        buildPlayerUpdate(playerSession);
    }

    private void sendMessages() throws InterruptedException {
        if (sendStack.isEmpty()) {
            Thread.sleep(1);
            return;
        }
        WsMessage message = sendStack.pop();
        websocket.sendMessage(message);
    }

    private void processMessages() throws InterruptedException {
        if (websocket.getReceiveStack().isEmpty()) {
            Thread.sleep(1);
            return;
        }
        WsMessage message = websocket.getReceiveStack().pop();
        processMessage(message);
    }

    @PostConstruct
    private void pushMessage() throws InterruptedException {
        ForkJoinPool threadPool = new ForkJoinPool(6);
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
                    processMessages();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
