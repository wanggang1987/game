/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private WebsocketController websocketController;

    private final Stack<WsMessage> sendStack = new Stack<>();
    private final Set<Long> playerUpdate = new HashSet<>();
    private final Map<Long, String> playerSession = new HashMap<>();

    public void addPlayerMoveMsg(Long id, RoleType type) {
        if (FuncUtils.equals(type, RoleType.PLAYER)) {
            playerUpdate.add(id);
        }
    }

    private void processMessage(WsMessage wsMessage) {
        if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.PLAYER_CREATE)) {
            Long playerId = createPlayer(wsMessage.getCreatePlayerMsg());
            playerSession.put(playerId, wsMessage.getSeesionId());
            playerUpdate.add(playerId);
        } else if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.PLAYER_LOGIN)) {
            playerSession.put(wsMessage.getPlayerId(), wsMessage.getSeesionId());
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

    @PostConstruct
    private void pushMessage() throws InterruptedException {
        ForkJoinPool threadPool = new ForkJoinPool(3);
        threadPool.submit(() -> {
            while (true) {
                try {
                    if (playerUpdate.isEmpty()) {
                        Thread.sleep(1);
                        continue;
                    }
                    List<Long> playerIds = new ArrayList<>();
                    playerIds.addAll(playerUpdate);
                    playerUpdate.clear();
                    playerIds.forEach(id -> {
                        WsMessage message = new WsMessage();
                        message.setMessageType(MessageType.PLAYER_ATTRIBUTE);
                        message.setPlayerId(id);
                        Player player = lifeCycle.onlinePlayer(id);
                        RoleMsg roleMsg = new RoleMsg();
                        FuncUtils.copyProperties(player, roleMsg);
                        message.setPlayerMsg(roleMsg);
                        sendStack.push(message);
                    });
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        threadPool.submit(() -> {
            while (true) {
                try {
                    if (sendStack.isEmpty()) {
                        Thread.sleep(1);
                        continue;
                    }
                    WsMessage message = sendStack.pop();
                    String sessionId = playerSession.get(message.getPlayerId());
                    message.setSeesionId(sessionId);
                    websocketController.sendMessage(message);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        threadPool.submit(() -> {
            while (true) {
                try {
                    if (websocketController.getReceiveStack().isEmpty()) {
                        Thread.sleep(1);
                        continue;
                    }
                    WsMessage message = websocketController.getReceiveStack().pop();
                    processMessage(message);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
