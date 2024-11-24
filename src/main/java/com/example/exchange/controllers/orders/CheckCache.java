/* (C)2024 */
package com.example.exchange.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.exchange.annotation.RequiresAuth;
import com.example.exchange.config.OrderBookConfig;
import com.example.exchange.db.RepositoryExample;
import com.example.exchange.model.OrderRequest;
import com.example.exchange.service.KafkaProducer;
import com.example.exchange.service.OrderService;
import com.example.exchange.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class CheckCache {
  private final RepositoryExample repositoryExample;
  private final ObjectMapper objectMapper;
  private final JwtUtil jwtUtil;
  private final KafkaProducer kafkaProducer;
  private final OrderBookConfig orderBookConfig;
  private final OrderService orderService;

  @Autowired
  public CheckCache(
      RepositoryExample repositoryExample,
      ObjectMapper objectMapper,
      JwtUtil jwtUtil,
      KafkaProducer kafkaProducer,
      OrderBookConfig orderBookConfig,
      OrderService orderService) {
    this.repositoryExample = repositoryExample;
    this.orderService = orderService;
    this.objectMapper = objectMapper;
    this.jwtUtil = jwtUtil;
    this.kafkaProducer = kafkaProducer;
    this.orderBookConfig = orderBookConfig;
  }

  @PostMapping("/check-cache")
  @RequiresAuth
  public ResponseEntity<String> checkCache(@RequestBody OrderRequest orderRequest) {
    try {
      RedisTemplate<String, String> redisTemplate = orderService.getRedisTemplate();

      // Get all trades from the list
      List<String> trades = redisTemplate.opsForList().range("fulfilled_trades", 0, -1);

      if (trades == null || trades.isEmpty()) {
        return ResponseEntity.ok().body("No trades found");
      }

      // Return all trades as a JSON array
      return ResponseEntity.ok().body(objectMapper.writeValueAsString(trades));

    } catch (JsonProcessingException e) {
      return ResponseEntity.internalServerError().body("Error processing trades data");
    }
  }

  @GetMapping("/candles/{symbol}")
  @RequiresAuth
  public ResponseEntity<String> getCandles(@PathVariable String symbol) {
    try {
      RedisTemplate<String, String> redisTemplate = orderService.getRedisTemplate();

      // Get all keys matching the pattern for this symbol
      Set<String> keys = redisTemplate.keys("candle:" + symbol + ":*");

      if (keys == null || keys.isEmpty()) {
        return ResponseEntity.ok().body("No candle data found for " + symbol);
      }

      List<String> candles = new ArrayList<>();
      for (String key : keys) {
        String candleData = redisTemplate.opsForValue().get(key);
        if (candleData != null) {
          candles.add(candleData);
        }
      }

      // Sort candles by timestamp
      Collections.sort(
          candles,
          (a, b) -> {
            try {
              JsonNode nodeA = objectMapper.readTree(a);
              JsonNode nodeB = objectMapper.readTree(b);
              return Long.compare(nodeA.get("timestamp").asLong(), nodeB.get("timestamp").asLong());
            } catch (JsonProcessingException e) {
              return 0;
            }
          });

      return ResponseEntity.ok().body(objectMapper.writeValueAsString(candles));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error processing candle data");
    }
  }
}
