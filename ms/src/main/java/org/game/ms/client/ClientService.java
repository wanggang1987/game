/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import javax.annotation.PostConstruct;
import javax.websocket.Session;
import org.game.ms.client.msg.MessageType;
import org.game.ms.client.msg.CreatePlayerMsg;
import org.game.ms.client.msg.WsMessage;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.msg.AttributeMsg;
import org.game.ms.client.msg.LocationMsg;
import org.game.ms.func.FuncUtils;
import org.game.ms.func.JsonUtils;
import org.game.ms.lifecycle.AutoPlayer;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.map.GridService;
import org.game.ms.map.WorldMap;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
import org.game.ms.role.Role;
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
    @Autowired
    private GridService gridService;

    private boolean buildMessage = false;
    private final Stack<WsMessage> sendStack = new Stack<>();
    private final BiMap<Long, String> playerSession = HashBiMap.create();
    private Set<Long> playerUpdate = new HashSet<>();
    private Set<Role> playerMove = new HashSet<>();
    private Set<Role> monsterMove = new HashSet<>();

    private void addPlayerSession(Long playerId, String sessionId) {
        if (playerSession.containsKey(playerId)) {
            return;
        }
        playerSession.put(playerId, sessionId);
    }

    public void removeSession(Session session) {
        playerSession.inverse().remove(session.getId());
    }

    public void addRoleMoveMsg(Role role) {
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            playerMove.add(role);
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            monsterMove.add(role);
        }
    }

    private void processMessage(WsMessage wsMessage) {
        if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.PLAYER_CREATE)) {
            Player player = createPlayer(wsMessage.getCreatePlayerMsg());
            addPlayerSession(player.getId(), wsMessage.getSeesionId());
            playerUpdate.add(player.getId());
            playerMove.add(player);
        } else if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.LOGIN)) {
            addPlayerSession(wsMessage.getPlayerId(), wsMessage.getSeesionId());
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

    public void readyBuildMessage() {
        buildMessage = true;
    }

    private void buildPlayerUpdate() {
        Collection<Long> temp = playerUpdate;
        playerUpdate = new HashSet<>();
        temp.forEach(id -> {
            if (playerSession.containsKey(id)) {
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.HERO_ATTRIBUTE);
                message.setSeesionId(playerSession.get(id));
                Player player = lifeCycle.onlinePlayer(id);
                AttributeMsg attribute = new AttributeMsg();
                FuncUtils.copyProperties(player, attribute);
                message.setAttributeMsg(attribute);
                sendStack.push(message);
            }
        });
    }

    private void buildRoleMove() {
        Collection<Role> tempPlayer = playerMove;
        playerMove = new HashSet<>();
        tempPlayer.forEach(player -> {
            if (playerSession.containsKey(player.getId())) {
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.HERO_LOCATION);
                message.setSeesionId(playerSession.get(player.getId()));
                LocationMsg locaionMsg = new LocationMsg();
                FuncUtils.copyProperties(player.getLocation(), locaionMsg);
                message.setLocationMsg(locaionMsg);
                sendStack.push(message);
            }
        });

        Collection<Role> tempMonster = monsterMove;
        monsterMove = new HashSet<>();
        tempMonster.forEach(monster -> {
            List<Long> gridPlayerIds = gridService.playerIdsInGrid(monster.getLocation().getGrid());
            gridPlayerIds.forEach(playerId -> {
                if (playerSession.containsKey(playerId)) {
                    WsMessage message = new WsMessage();
                    message.setMessageType(MessageType.MONSTER_LOCATION);
                    message.setSeesionId(playerSession.get(playerId));
                    LocationMsg locaionMsg = new LocationMsg();
                    locaionMsg.setId(monster.getId());
                    FuncUtils.copyProperties(monster.getLocation(), locaionMsg);
                    message.setLocationMsg(locaionMsg);
                    sendStack.push(message);
                }
            });
        });
    }

    private void buildMessages() throws InterruptedException {
        Thread.sleep(1);
        if (!buildMessage) {
            return;
        }
        buildMessage = false;
        if (playerUpdate.isEmpty()
                && playerMove.isEmpty()
                && monsterMove.isEmpty()) {
            Thread.sleep(1);
            return;
        }
        buildPlayerUpdate();
        buildRoleMove();
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
