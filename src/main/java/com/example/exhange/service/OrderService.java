/* (C)2024 */
package com.example.exchange.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.exchange.config.OrderBookConfig;
import com.example.exchange.model.OrderRequest;

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

  public void generateAndSendOrders(int count) {
    for (int i = 0; i < count; i++) {
      OrderRequest order = generateRandomOrder();
      String redisKey = REDIS_KEY_PREFIX + order.id;

      Boolean isNewOrder = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", ORDER_EXPIRATION);

      if (Boolean.TRUE.equals(isNewOrder)) {
        try {
          OrderBook orderBook = orderBooks.get(order.symbol);
          System.out.println("Inserting order for symbol: " + order.symbol);

          if (orderBook != null) {
            List<Trade> trades = orderBook.insertOrder(order);
            System.out.println("Sent order: " + order.id + " for " + order.symbol);
            printTrades(trades);
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

  private OrderRequest generateRandomOrder() {
    String symbol = SYMBOLS[random.nextInt(SYMBOLS.length)];
    OrderRequest.OrderType type =
        random.nextBoolean() ? OrderRequest.OrderType.BUY : OrderRequest.OrderType.SELL;
    int notionalAmount = 1 + random.nextInt(1000); // Random amount between 1 and 1000
    String id = symbol + "-" + UUID.randomUUID().toString();
    long price = 100 + random.nextInt(400); // Random price between 100 and 499

    return new OrderRequest(type, notionalAmount, id, price, symbol);
  }

  public void generateThousandTrades() {
    generateAndSendOrders(100000);
    printAllOrderBookStatuses();
  }

  private void printTrades(List<Trade> trades) {
    for (Trade trade : trades) {
      System.out.println("Executed trade: " + trade);
    }
  }

  public void printAllOrderBookStatuses() {
    for (String symbol : SYMBOLS) {
      OrderBook orderBook = orderBooks.get(symbol);
      System.out.println(symbol + " Order Book Status:");
      System.out.println("Highest Buy Order: " + orderBook.getHighestBuyOrder());
      System.out.println("Lowest Sell Order: " + orderBook.getLowestSellOrder());
      System.out.println("Buy Orders Size: " + orderBook.getBuyOrdersSize());
      System.out.println("Sell Orders Size: " + orderBook.getSellOrdersSize());
      System.out.println();
    }
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
          printTrades(trades);
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
