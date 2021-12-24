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
import javax.websocket.Session;
import lombok.Data;
import org.game.ms.client.msg.WsMessage;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.msg.AttributeRequest;
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
public class MessageService {

    @Autowired
    private BuildMessageService buildMessageService;

    private final BiMap<Long, String> playerSession = HashBiMap.create();
    private final Queue<WsMessage> receiveQueue = new ConcurrentLinkedQueue<>();
    private final Queue<WsMessage> sendQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Role> roleMove = new ConcurrentLinkedQueue<>();
    private final Queue<Role> flashGrid = new ConcurrentLinkedQueue<>();
    private final Queue<Role> roleDie = new ConcurrentLinkedDeque<>();
    private final Queue<AttributeRequest> roleAttribute = new ConcurrentLinkedDeque<>();
    private final Queue<Long> heroUpdate = new ConcurrentLinkedDeque<>();

    public void heroUpdate(Long playerId) {
        heroUpdate.add(playerId);
    }

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
        }
        roleDie.add(role);
    }

    public void addRoleMoveMsg(Role role) {
        roleMove.add(role);
    }

    public void addRoleToGridMsg(Role role) {
        if (FuncUtils.equals(role.getRoleType(), RoleType.PLAYER)) {
            flashGrid.add(role);
            roleMove.add(role);
        } else if (FuncUtils.equals(role.getRoleType(), RoleType.MONSTER)) {
            roleMove.add(role);
        }
    }

}
