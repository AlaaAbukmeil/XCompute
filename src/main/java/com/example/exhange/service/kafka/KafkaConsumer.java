/* (C)2024 */
package com.example.exchange.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.exchange.model.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumer {
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private final ObjectMapper objectMapper;
  private final OrderService orderService;

  public KafkaConsumer(ObjectMapper objectMapper, OrderService orderService) {
    this.objectMapper = objectMapper;
    this.orderService = orderService;
  }

  @KafkaListener(topics = "test", groupId = "myGroup")
  public void listen(String message) {
    logger.warn("This is a Secret message " + message);
  }

  @KafkaListener(topics = "orders", groupId = "order-processing-group")
  public void processOrder(String orderJson) throws JsonProcessingException {
    OrderRequest order = objectMapper.readValue(orderJson, OrderRequest.class);
    orderService.processOrder(order);
  }

  @KafkaListener(topics = "print-order-books", groupId = "order-processing-group")
  public void printOrderBooks() throws Exception {
    orderService.printAllOrderBookStatuses();
  }
}
