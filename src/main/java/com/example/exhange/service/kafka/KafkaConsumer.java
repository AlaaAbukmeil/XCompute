/* (C)2024 */
package com.example.exchange.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.exchange.config.OrderBookConfig;
import com.example.exchange.model.OrderBookSummary;
import com.example.exchange.model.OrderRequest;
import com.example.exchange.websocket.OrderBookWebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumer {
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private final ObjectMapper objectMapper;
  private final OrderService orderService;
  private final OrderBookWebSocketHandler webSocketHandler;
  private final OrderBookConfig orderBookConfig;

  public KafkaConsumer(
      ObjectMapper objectMapper,
      OrderService orderService,
      OrderBookWebSocketHandler webSocketHandler,
      OrderBookConfig orderBookConfig) {
    this.objectMapper = objectMapper;
    this.orderService = orderService;
    this.webSocketHandler = webSocketHandler;
    this.orderBookConfig = orderBookConfig;
  }

  @KafkaListener(topics = "test", groupId = "myGroup")
  public void listen(String message) {
    logger.warn("This is a Secret message " + message);
  }

  @KafkaListener(topics = "orders", groupId = "order-processing-group")
  public void processOrder(String orderJson) throws JsonProcessingException {
    OrderRequest order = objectMapper.readValue(orderJson, OrderRequest.class);
    orderService.processOrder(order);
    OrderBook orderBook = orderBookConfig.getOrderBook(order.symbol);
    OrderBookSummary orderBookSummary = orderBook.getOrderBookSummary();
    Map<String, OrderBookSummary> update = new HashMap<>();
    update.put(order.symbol, orderBookSummary);

    String jsonSummary = objectMapper.writeValueAsString(update);
    webSocketHandler.broadcastUpdate(jsonSummary);
  }

  @KafkaListener(topics = "print-order-books", groupId = "order-processing-group")
  public void printOrderBooks() throws Exception {
    orderService.printAllOrderBookStatuses();
  }

  @KafkaListener(topics = "order-book-updates", groupId = "order-book-group")
  public void consume(String message) throws JsonProcessingException {
    webSocketHandler.broadcastUpdate(message);
  }
}
