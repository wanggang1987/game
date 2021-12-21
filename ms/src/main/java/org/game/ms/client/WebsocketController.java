/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.client;

import org.game.ms.client.msg.WsMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.func.JsonUtils;
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
    private final static Stack<WsMessage> receiveStack = new Stack<>();

    public Stack<WsMessage> getReceiveStack() {
        return receiveStack;
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
        receiveStack.add(wsMessage);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(WsMessage message) {
        if (FuncUtils.isEmpty(message.getSeesionId())) {
            return;
        }
        Session session = clients.get(message.getSeesionId());
        if (FuncUtils.isEmpty(session)) {
            return;
        }
        try {
            String msg = JsonUtils.bean2json(message);
            session.getBasicRemote().sendText(msg);
            log.debug("sendMessage {}", msg);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
