/* (C)2024 */
package com.example.exchange.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.exchange.model.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

  public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public void sendOrder(String topic, OrderRequest order) throws JsonProcessingException {
    String orderJson = objectMapper.writeValueAsString(order);
    logger.debug("Producer Send an order: " + topic + "_" + order.symbol);
    kafkaTemplate.send(topic + "_" + order.symbol, String.valueOf(order.id), orderJson);
  }
}
