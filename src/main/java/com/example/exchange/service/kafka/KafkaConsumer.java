/* (C)2024 */
package com.example.exchange.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
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
  String[] symbols = {"AAPL", "GOOGL", "MSFT", "AMZN", "FB"};

  public KafkaConsumer(
      ObjectMapper objectMapper,
      OrderService orderService,
      OrderBookWebSocketHandler orderBookSocketHandler,
      PriceChartsWebSocketHandler priceChartsSocketHandler,
      MatchingEngineConfig matchingEngineConfig,
      MatchingEngineJNI matchingEngineJNI,
      RedisTemplate<String, String> redisTemplate) {
    this.objectMapper = objectMapper;
    this.orderService = orderService;
    this.orderBookSocketHandler = orderBookSocketHandler;
    this.matchingEngineConfig = matchingEngineConfig;
    this.matchingEngineJNI = matchingEngineJNI;
    this.priceChartsSocketHandler = priceChartsSocketHandler;
    this.redisTemplate = redisTemplate;
  }

  @KafkaListener(topics = "test", groupId = "myGroup")
  public void listen(String message) {
    logger.warn("This is a Secret message " + message);
  }

  @KafkaListener(topics = "orders", groupId = "order-processing-group")
  public void processOrder(String orderJson) throws JsonProcessingException {

    OrderRequest order = objectMapper.readValue(orderJson, OrderRequest.class);
    // logger.info("Trying to insert order: " + order.id);
    long pointer = matchingEngineConfig.getMatchingEnginePointer(order.symbol);
    orderService.processOrder(order, pointer);
  }

  @Scheduled(fixedRate = 1000) // 1000ms = 1 second
  public void broadcastUpdates() {
    try {
      for (String symbol : symbols) {
        long pointer = matchingEngineConfig.getMatchingEnginePointer(symbol);
        String summary = matchingEngineJNI.getMatchingEngineSummary(pointer);
        OrderBookSummary orderBookSummary = objectMapper.readValue(summary, OrderBookSummary.class);
        update.put(symbol, orderBookSummary);
        String jsonSummary = objectMapper.writeValueAsString(update);
        orderBookSocketHandler.broadcastUpdate(jsonSummary);
      }

    } catch (JsonProcessingException e) {
      // Handle exception

    }
  }

  @Scheduled(fixedRate = 1000)
  public void broadcastPriceUpdates() {
    try {
      ObjectNode allSymbolsData = objectMapper.createObjectNode();

      for (String symbol : symbols) {
        String symbolKey = "candle:" + symbol;
        String candlesJson = redisTemplate.opsForValue().get(symbolKey);

        if (candlesJson != null) {
          JsonNode candlesNode = objectMapper.readTree(candlesJson);
          allSymbolsData.set(symbol, candlesNode);
        } else {
          // If no data exists for the symbol, set an empty object
          allSymbolsData.set(symbol, objectMapper.createObjectNode());
        }
      }

      String priceUpdatesJson = objectMapper.writeValueAsString(allSymbolsData);
      priceChartsSocketHandler.broadcastUpdate(priceUpdatesJson);

    } catch (Exception e) {
      logger.error("Error broadcasting price updates: ", e);
    }
  }
}
