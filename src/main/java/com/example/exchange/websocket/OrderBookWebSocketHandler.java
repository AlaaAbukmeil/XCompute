/* (C)2024 */
package com.example.exchange.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class OrderBookWebSocketHandler extends TextWebSocketHandler {
  private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    // System.out.println("New WebSocket connection established: " + session.getId());
    sessions.add(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    // System.out.println("WebSocket connection closed: ");

    sessions.remove(session);
  }

  public void broadcastUpdate(String message) {
    // System.out.println("Broadcasting update: " + message);
    TextMessage textMessage = new TextMessage(message);
    sessions.forEach(
        session -> {
          try {
            if (session.isOpen()) {
              session.sendMessage(textMessage);
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }
}
