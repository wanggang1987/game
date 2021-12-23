/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import javax.annotation.PostConstruct;
import javax.websocket.Session;
import org.game.ms.client.msg.MessageType;
import org.game.ms.client.msg.CreatePlayerMsg;
import org.game.ms.client.msg.WsMessage;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.msg.AttributeMsg;
import org.game.ms.client.msg.LocationMsg;
import org.game.ms.client.msg.RoleDieMsg;
import org.game.ms.func.FuncUtils;
import org.game.ms.func.JsonUtils;
import org.game.ms.lifecycle.AutoPlayer;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.map.GridService;
import org.game.ms.map.WorldMap;
import org.game.ms.monster.Monster;
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
    private final Queue<WsMessage> sendQueue = new ConcurrentLinkedQueue<>();
    private final BiMap<Long, String> playerSession = HashBiMap.create();
    private final Queue<Long> playerUpdate = new ConcurrentLinkedQueue<>();
    private final Queue<Role> playerMove = new ConcurrentLinkedQueue<>();
    private final Queue<Role> monsterMove = new ConcurrentLinkedQueue<>();
    private final Queue<Role> flashGrid = new ConcurrentLinkedQueue<>();
    private final Queue<Role> monsterDie = new ConcurrentLinkedDeque<>();

    private void addPlayerSession(Long playerId, String sessionId) {
        if (playerSession.containsKey(playerId)) {
            return;
        }
        playerSession.put(playerId, sessionId);
    }

    public void removeSession(Session session) {
        playerSession.inverse().remove(session.getId());
    }

    public void addRoleDieMsg(Role role) {
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            monsterDie.add(role);
        }
    }

    public void addRoleMoveMsg(Role role) {
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            playerMove.add(role);
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            monsterMove.add(role);
        }
    }

    public void addRoleToGridMsg(Role role) {
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            flashGrid.add(role);
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
        Long playerId = playerUpdate.poll();
        if (FuncUtils.isEmpty(playerId)) {
            return;
        }
        if (!playerSession.containsKey(playerId)) {
            return;
        }
        WsMessage message = new WsMessage();
        message.setMessageType(MessageType.HERO_ATTRIBUTE);
        message.setSeesionId(playerSession.get(playerId));
        Player player = lifeCycle.onlinePlayer(playerId);
        AttributeMsg attribute = new AttributeMsg();
        FuncUtils.copyProperties(player, attribute);
        message.setAttributeMsg(attribute);
        sendQueue.offer(message);
    }

    private void buildPlayerMove() {
        Role player = playerMove.poll();
        if (FuncUtils.isEmpty(player)) {
            return;
        }
        if (!playerSession.containsKey(player.getId())) {
            return;
        }
        WsMessage message = new WsMessage();
        message.setMessageType(MessageType.HERO_LOCATION);
        message.setSeesionId(playerSession.get(player.getId()));
        LocationMsg locaionMsg = new LocationMsg();
        FuncUtils.copyProperties(player.getLocation(), locaionMsg);
        message.setLocationMsg(locaionMsg);
        sendQueue.offer(message);
    }

    private void buildMonsterMove() {
        Role monster = monsterMove.poll();
        if (FuncUtils.isEmpty(monster)) {
            return;
        }
        List<Long> gridPlayerIds = gridService.playerIdsInGrid(monster.getLocation().getGrid());
        for (Long playerId : gridPlayerIds) {
            if (playerSession.containsKey(playerId)) {
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.MONSTER_LOCATION);
                message.setSeesionId(playerSession.get(playerId));
                LocationMsg locaionMsg = new LocationMsg();
                locaionMsg.setId(monster.getId());
                FuncUtils.copyProperties(monster.getLocation(), locaionMsg);
                message.setLocationMsg(locaionMsg);
                sendQueue.offer(message);
            }
        }
    }

    private void buildPlayerGrid() {
        Role player = flashGrid.poll();
        if (FuncUtils.isEmpty(player)) {
            return;
        }
        if (!playerSession.containsKey(player.getId())) {
            return;
        }
        List<Long> gridMonsterIds = gridService.monsterIdsInGrid(player.getLocation().getGrid());
        for (Long monsterId : gridMonsterIds) {
            Monster monster = lifeCycle.onlineMonster(monsterId);
            WsMessage message = new WsMessage();
            message.setMessageType(MessageType.MONSTER_LOCATION);
            message.setSeesionId(playerSession.get(player.getId()));
            LocationMsg locaionMsg = new LocationMsg();
            locaionMsg.setId(monster.getId());
            FuncUtils.copyProperties(monster.getLocation(), locaionMsg);
            message.setLocationMsg(locaionMsg);
            sendQueue.offer(message);
        }
    }

    private void buildMonsterDie() {
        Role monster = monsterDie.poll();
        if (FuncUtils.isEmpty(monster)) {
            return;
        }
        List<Long> gridPlayerIds = gridService.playerIdsInGrid(monster.getLocation().getGrid());
        for (Long playerId : gridPlayerIds) {
            if (playerSession.containsKey(playerId)) {
                WsMessage message = new WsMessage();
                message.setMessageType(MessageType.MONSTER_DIE);
                message.setSeesionId(playerSession.get(playerId));
                RoleDieMsg roleDieMsg = new RoleDieMsg();
                roleDieMsg.setId(monster.getId());
                message.setRoleDieMsg(roleDieMsg);
                sendQueue.offer(message);
            }
        }
    }

    private void buildMessages() throws InterruptedException {
        Thread.sleep(1);
        if (!buildMessage) {
            return;
        }
        while (FuncUtils.notEmpty(playerUpdate)
                || FuncUtils.notEmpty(playerMove)
                || FuncUtils.notEmpty(monsterMove)
                || FuncUtils.notEmpty(flashGrid)) {
            buildPlayerUpdate();
            buildPlayerMove();
            buildMonsterMove();
            buildPlayerGrid();
            buildMonsterDie();
        }
        buildMessage = false;
    }

    private void sendMessages() throws InterruptedException {
        WsMessage message = sendQueue.peek();
        if (FuncUtils.isEmpty(message)) {
            Thread.sleep(1);
            return;
        }
        message = sendQueue.poll();
        websocket.sendMessage(message);
    }

    private void processMessages() throws InterruptedException {
        WsMessage message = websocket.getReceiveQueue().peek();
        if (FuncUtils.isEmpty(message)) {
            Thread.sleep(1);
            return;
        }
        message = websocket.getReceiveQueue().poll();
        processMessage(message);
    }

    @PostConstruct
    private void pushMessage() throws InterruptedException {
        ForkJoinPool threadPool = new ForkJoinPool(3);
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
