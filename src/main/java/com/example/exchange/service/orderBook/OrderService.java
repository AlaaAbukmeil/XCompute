/* (C)2024 */
package com.example.exchange.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.exchange.config.OrderBookConfig;
import com.example.exchange.model.OrderRequest;
import com.example.exchange.model.Trade;

@Service
public class OrderService {
  private final RedisTemplate<String, String> redisTemplate;
  private final Map<String, OrderBook> orderBooks;
  private static final String REDIS_KEY_PREFIX = "order:";
  private static final Duration ORDER_EXPIRATION = Duration.ofHours(24);
  private static final String[] SYMBOLS = {"AAPL", "GOOGL", "MSFT", "AMZN", "FB"};
  private static final Random random = new Random();

  public OrderService(
      RedisTemplate<String, String> redisTemplate, OrderBookConfig orderBookConfig) {
    this.redisTemplate = redisTemplate;
    this.orderBooks = orderBookConfig.getOrderBooks();
  }

  public void processOrder(OrderRequest order) {
    String redisKey = REDIS_KEY_PREFIX + order.id;

    Boolean isNewOrder = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", ORDER_EXPIRATION);

    if (Boolean.TRUE.equals(isNewOrder)) {
      try {
        OrderBook orderBook = orderBooks.get(order.symbol);
        System.out.println("Inserting order for symbol: " + order.symbol);

        if (orderBook != null) {
          // Create a new OrderRequest with the original notional amount set
          OrderRequest newOrder =
              new OrderRequest(
                  order.type, order.notionalAmount, order.id, order.price, order.symbol);
          List<Trade> trades = orderBook.insertOrder(newOrder);
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
}
