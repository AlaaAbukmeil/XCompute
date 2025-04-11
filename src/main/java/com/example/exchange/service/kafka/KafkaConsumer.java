/* (C)2024 */
package com.example.exchange.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.exchange.config.MatchingEngineConfig;
import com.example.exchange.jni.MatchingEngineJNI;
import com.example.exchange.model.OrderBookSummary;
import com.example.exchange.model.OrderRequest;
import com.example.exchange.websocket.OrderBookWebSocketHandler;
import com.example.exchange.websocket.PriceChartsWebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class KafkaConsumer {
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private final ObjectMapper objectMapper;
  private final OrderService orderService;
  private final OrderBookWebSocketHandler orderBookSocketHandler;
  private final PriceChartsWebSocketHandler priceChartsSocketHandler;
  private final MatchingEngineConfig matchingEngineConfig;
  private final MatchingEngineJNI matchingEngineJNI;
  private final RedisTemplate<String, String> redisTemplate;
  private Map<String, OrderBookSummary> update = new HashMap<>();
  private ObjectNode allPriceData;

  String[] symbols = {"AAPL", "GOOGL"};
  public KafkaConsumer(
      ObjectMapper objectMapper,
      OrderService orderService,
      OrderBookWebSocketHandler orderBookSocketHandler,
      PriceChartsWebSocketHandler priceChartsSocketHandler,
      MatchingEngineConfig matchingEngineConfig,
      MatchingEngineJNI matchingEngineJNI,
      RedisTemplate<String, String> redisTemplate
      
      ) {
    this.objectMapper = objectMapper;
    this.orderService = orderService;
    this.orderBookSocketHandler = orderBookSocketHandler;
    this.matchingEngineConfig = matchingEngineConfig;
    this.matchingEngineJNI = matchingEngineJNI;
    this.priceChartsSocketHandler = priceChartsSocketHandler;
    this.redisTemplate = redisTemplate;
    this.allPriceData =  objectMapper.createObjectNode();
  }

  @KafkaListener(topics = "test", groupId = "myGroup")
  public void listen(String message) {
    logger.warn("This is a Secret message " + message);
  }

  // @KafkaListener(topics = "orders", groupId = "order-processing-group")
  // public void processOrder(String orderJson) throws JsonProcessingException {

  //   OrderRequest order = objectMapper.readValue(orderJson, OrderRequest.class);
  //   // logger.info("Trying to insert order: " + order.id);
  //   long pointer = matchingEngineConfig.getMatchingEnginePointer(order.symbol);
  //   orderService.processOrder(order, pointer);
  // }

  @KafkaListener(topics = "orders_matched", groupId = "myGroup")
  public void listenOrderSummaryUpdates(ConsumerRecord<String, String> record) throws JsonProcessingException {
    String key = record.key();
    String message = record.value();
    Map<String, OrderBookSummary> newUpdateMap = objectMapper.readValue(message, 
      new TypeReference<Map<String, OrderBookSummary>>() {}
  );
    update.put(key, newUpdateMap.get(key));
    // logger.info("key: " + key + "\n\n new map: " + newUpdateMap.get(key));
  }
  @KafkaListener(topics = "new_prices", groupId = "myGroup")
  public void listenNewPriceUpdates(ConsumerRecord<String, String> record) throws JsonProcessingException {
    String key = record.key();
    String message = record.value();
    ObjectNode pricesData = objectMapper.readValue(message, 
      new TypeReference<ObjectNode>() {}
  );
  if(pricesData.get(key) != null){

    allPriceData.set(key, pricesData.get(key));
  }else{
    allPriceData.set(key, null);

  }

    logger.info("key: " + key + "\n\n new prices: " + allPriceData.get(key));
  }

  @Scheduled(fixedRate = 1000) // 1000ms = 1 second
  public void broadcastUpdates() {
    try {
      
        String jsonSummary = objectMapper.writeValueAsString(update);
        orderBookSocketHandler.broadcastUpdate(jsonSummary);

    } catch (JsonProcessingException e) {
      // Handle exception

    }
  }

  @Scheduled(fixedRate = 1000)
  public void broadcastPriceUpdates() {
    try {
      

      String priceUpdatesJson = objectMapper.writeValueAsString(allPriceData);
      priceChartsSocketHandler.broadcastUpdate(priceUpdatesJson);

    } catch (Exception e) {
      logger.error("Error broadcasting price updates: ", e);
    }
  }

 
}
