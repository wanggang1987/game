/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.func.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author gangwang
 */
@Slf4j
@ServerEndpoint(value = "/ws")
@Controller
public class WebsocketController {

    private final Map<String, Session> clients = new HashMap<>();
    private final Map<Long, String> players = new HashMap<>();
    @Autowired
    private ClientService clientService;

    @OnOpen
    public void onOpen(Session session) {
        clients.put(session.getId(), session);
        // 先鉴权，如果鉴权通过则存储WebsocketSession，否则关闭连接，这里省略了鉴权的代码 
        log.debug("session open. ID:{}", session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session.getId());
        log.debug("session close. ID:{}", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        WsMessage wsMessage = JsonUtils.json2bean(message, WsMessage.class);
        if (FuncUtils.equals(wsMessage.getMessageType(), MessageType.PLAYER_LOGIN)) {
            players.put(wsMessage.getPlayerId(), session.getId());
        }
        clientService.processMessage(wsMessage);
        log.debug("get client msg. ID:{} msg:{}", session.getId(), message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(WsMessage message) {
        String sessionId = players.get(message.getPlayerId());
        if (FuncUtils.isEmpty(sessionId)) {
            return;
        }
        Session session = clients.get(sessionId);
        try {
            session.getBasicRemote().sendText(JsonUtils.bean2json(message));
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
