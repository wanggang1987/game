/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client;

import java.util.ArrayList;
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
    private WebsocketController websocketController;

    private ForkJoinPool threadPool;
    private Stack<WsMessage> messageStack = new Stack<>();
    private Set<Long> playerUpdate = new HashSet<>();

    public void addPlayerMoveMsg(Long id, RoleType type) {
        if (FuncUtils.equals(type, RoleType.PLAYER)) {
            playerUpdate.add(id);
        }
    }

    public void processMessage(WsMessage wsMessage) {
        if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.PLAYER_CREATE)) {
            Long playerId = createPlayer(wsMessage.getCreatePlayerMsg());
            websocketController.addPlayer(playerId, wsMessage.getSeesionId());
            playerUpdate.add(playerId);
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
        threadPool = new ForkJoinPool(2);
        threadPool.submit(() -> {
            //start the thread
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
                        messageStack.push(message);
                    });
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        threadPool.submit(() -> {
            //start the thread
            while (true) {
                try {
                    if (messageStack.isEmpty()) {
                        Thread.sleep(1);
                        continue;
                    }
                    WsMessage message = messageStack.pop();
                    websocketController.sendMessage(message);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
