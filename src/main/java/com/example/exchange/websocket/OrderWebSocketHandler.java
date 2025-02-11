/* (C)2024 */
package com.example.exchange.websocket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.exchange.model.OrderRequest;
import com.example.exchange.service.AuthenticationService;
import com.example.exchange.service.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrderWebSocketHandler extends TextWebSocketHandler {

  private final Logger logger = LoggerFactory.getLogger(OrderWebSocketHandler.class);
  private final ObjectMapper objectMapper;
  private final KafkaProducer kafkaProducer;

  private AuthenticationService authenticationService;
  private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

  @Autowired
  public OrderWebSocketHandler(
      ObjectMapper objectMapper,
      KafkaProducer kafkaProducer,
      AuthenticationService authenticationService) {
    this.objectMapper = objectMapper;
    this.kafkaProducer = kafkaProducer;
    this.authenticationService = authenticationService;
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    try {
      OrderRequest orderRequest = objectMapper.readValue(message.getPayload(), OrderRequest.class);
      kafkaProducer.sendOrder("orders", orderRequest);

      String responseJson =
          objectMapper.writeValueAsString(
              Map.of(
                  "status",
                  "Order received",
                  "orderId",
                  orderRequest.getId(),
                  "orderRequest",
                  orderRequest));

      session.sendMessage(new TextMessage(responseJson));
    } catch (Exception e) {
      logger.error("Error processing order", e);
      try {
        session.sendMessage(new TextMessage("{\"error\": \"Error processing order\"}"));
      } catch (Exception se) {
        logger.error("Error sending error message", se);
      }
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    // System.out.println("New WebSocket connection established: " + session.getId());
    sessions.add(session);
  }
}
