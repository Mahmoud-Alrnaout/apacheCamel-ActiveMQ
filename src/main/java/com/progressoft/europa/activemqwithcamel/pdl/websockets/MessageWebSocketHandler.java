package com.progressoft.europa.activemqwithcamel.pdl.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
public class MessageWebSocketHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public static void broadcastMessage(String correlationId, Object message) {
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    Map<String, Object> response = Map.of("correlationId", correlationId, "data", message, "timestamp", System.currentTimeMillis());
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                }
            } catch (IOException e) {
                log.error("Error while sending message to websocket {}", e.getMessage());
            }
        });
    }

}