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
import com.example.exchange.websocket.PriceChartsWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final OrderWebSocketHandler orderWebSocketHandler;
  private final PriceChartsWebSocketHandler priceChartsSocketHandler;
  private final OrderBookWebSocketHandler orderBookWebSocketHandler;

  private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

  @Autowired
  public WebSocketConfig(
      OrderWebSocketHandler orderWebSocketHandler,
      PriceChartsWebSocketHandler priceChartsSocketHandler,
      OrderBookWebSocketHandler orderBookWebSocketHandler) {

    this.orderWebSocketHandler = orderWebSocketHandler;
    this.priceChartsSocketHandler = priceChartsSocketHandler;
    this.orderBookWebSocketHandler = orderBookWebSocketHandler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    System.out.println("Registering WebSocket handlers");
    logger.info("Registering WebSocket handlers");
    registry.addHandler(orderWebSocketHandler, "/websocket/orders").setAllowedOrigins("*");
    logger.info("WebSocket handler registered for path: /websocket/orders");
    registry.addHandler(orderBookWebSocketHandler, "/websocket/orderbook").setAllowedOrigins("*");
    logger.info("WebSocket handler registered for path: /websocket/orderbook");
    registry
        .addHandler(priceChartsSocketHandler, "/websocket/minute-price-charts")
        .setAllowedOrigins("*");
    logger.info("WebSocket handler registered for path: /websocket/minute-price-charts");
  }

  @PostConstruct
  public void init() {
    logger.warn("WebSocketConfig bean created");
  }
}
