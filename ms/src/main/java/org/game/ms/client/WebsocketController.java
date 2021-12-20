/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client;

import org.game.ms.client.msg.WsMessage;
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

    private final static Map<String, Session> clients = new HashMap<>();
    private final static Map<Long, String> players = new HashMap<>();
    private static ClientService clientService;

    @Autowired
    public void setClientService(ClientService clientService) {
        WebsocketController.clientService = clientService;
    }

    public void addPlayer(Long playerId, String seessionId) {
        players.put(playerId, seessionId);
    }

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
        log.debug("get client msg. ID:{} msg:{}", session.getId(), message);
        WsMessage wsMessage = JsonUtils.json2bean(message, WsMessage.class);
        wsMessage.setSeesionId(session.getId());
        clientService.processMessage(wsMessage);
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
        if (FuncUtils.isEmpty(session)) {
            return;
        }
        try {
            String msg = JsonUtils.bean2json(message);
            log.debug("sendMessage {}", msg);
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
