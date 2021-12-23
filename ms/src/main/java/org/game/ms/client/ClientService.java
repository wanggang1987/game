/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import javax.annotation.PostConstruct;
import javax.websocket.Session;
import lombok.Data;
import org.game.ms.client.msg.WsMessage;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.role.Role;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Data
@Slf4j
@Service
public class ClientService {

    @Autowired
    private BuildMessageService buildMessageService;
    @Autowired
    private ProcessMessageService processMessageService;

    private final BiMap<Long, String> playerSession = HashBiMap.create();
    private final Queue<WsMessage> receiveQueue = new ConcurrentLinkedQueue<>();
    private final Queue<WsMessage> sendQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Long> playerUpdate = new ConcurrentLinkedQueue<>();
    private final Queue<Role> playerMove = new ConcurrentLinkedQueue<>();
    private final Queue<Role> monsterMove = new ConcurrentLinkedQueue<>();
    private final Queue<Role> flashGrid = new ConcurrentLinkedQueue<>();
    private final Queue<Role> monsterDie = new ConcurrentLinkedDeque<>();

    public void addPlayerSession(Long playerId, String sessionId) {
        if (playerSession.containsKey(playerId)) {
            return;
        }
        playerSession.put(playerId, sessionId);
    }

    public void removeSession(Session session) {
        playerSession.inverse().remove(session.getId());
    }

    public void readyBuildMessage() {
        buildMessageService.readyBuildMessage();
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

    @PostConstruct
    private void pushMessage() throws InterruptedException {
        ForkJoinPool threadPool = new ForkJoinPool(3);
        threadPool.submit(() -> {
            while (true) {
                try {
                    buildMessageService.buildMessages();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        threadPool.submit(() -> {
            while (true) {
                try {
                    processMessageService.sendMessages();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        threadPool.submit(() -> {
            while (true) {
                try {
                    processMessageService.receiveMessages();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
