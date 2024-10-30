/* (C)2024 */
package com.example.exchange.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.exchange.websocket.OrderBookWebSocketHandler;
import com.example.exchange.websocket.OrderWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final OrderBookWebSocketHandler orderBookWebSocketHandler;
  private final OrderWebSocketHandler orderWebSocketHandler;
  private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

  @Autowired
  public WebSocketConfig(
      OrderBookWebSocketHandler orderBookWebSocketHandler,
      OrderWebSocketHandler orderWebSocketHandler) {
    this.orderBookWebSocketHandler = orderBookWebSocketHandler;
    this.orderWebSocketHandler = orderWebSocketHandler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    System.out.println("Registering WebSocket handlers");
    logger.info("Registering WebSocket handlers");
    registry.addHandler(orderBookWebSocketHandler, "/websocket/orderbook").setAllowedOrigins("*");
    logger.info("WebSocket handler registered for path: /websocket/orderbook");
    registry.addHandler(orderWebSocketHandler, "/websocket/ordes").setAllowedOrigins("*");
    logger.info("WebSocket handler registered for path: /websocket/ordes");
  }

  @PostConstruct
  public void init() {
    logger.warn("WebSocketConfig bean created");
  }
}
