/* (C)2024 */
package com.example.exchange.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.exchange.jni.MatchingEngineJNI;
import com.example.exchange.model.OrderRequest;

@Service
public class OrderService {
  private final RedisTemplate<String, String> redisTemplate;
  private final MatchingEngineJNI matchingEngineJNI;
  private static final String REDIS_KEY_PREFIX = "order:";
  private static final Duration ORDER_EXPIRATION = Duration.ofHours(24);
  private static final String[] SYMBOLS = {"AAPL", "GOOGL", "MSFT", "AMZN", "FB"};
  private static final Random random = new Random();

  public OrderService(
      RedisTemplate<String, String> redisTemplate, MatchingEngineJNI matchingEngineJNI) {
    this.redisTemplate = redisTemplate;
    this.matchingEngineJNI = matchingEngineJNI;
  }

  public void processOrder(OrderRequest order, long pointer) {
    String redisKey = REDIS_KEY_PREFIX + order.id;

    Boolean isNewOrder = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", ORDER_EXPIRATION);

    if (Boolean.TRUE.equals(isNewOrder)) {
      try {

        if (pointer != -1) {
          matchingEngineJNI.insertOrder(
              pointer, order.id, order.type, order.price, order.notionalAmount);
        } else {
          System.out.println("Error: No order book found for symbol " + order.symbol);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Duplicate order detected: " + order.id);
    }
  }

  public RedisTemplate<String, String> getRedisTemplate() {
    return this.redisTemplate;
  }
}
