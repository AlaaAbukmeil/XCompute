/* (C)2024 */
package com.example.exchange.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.example.exchange.model.OrderBookSummary;
import com.example.exchange.model.OrderRequest;
import com.example.exchange.model.Trade;

@Service
public class OrderBook {

  public String symbol;

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  private final PriorityQueue<OrderRequest> buyOrders =
      new PriorityQueue<>(
          Comparator.comparingLong(OrderRequest::getPrice)
              .reversed()
              .thenComparing(OrderRequest::getId));

  private final PriorityQueue<OrderRequest> sellOrders =
      new PriorityQueue<>(
          Comparator.comparingLong(OrderRequest::getPrice).thenComparing(OrderRequest::getId));

  private final Queue<OrderRequest> lastTenFulfilledOrders = new LinkedList<>();

  public List<Trade> insertOrder(OrderRequest order) {
    List<Trade> trades = new ArrayList<>();

    if (order.type == "BUY") {
      trades.addAll(matchBuyOrder(order));
    } else if (order.type == "SELL") {
      trades.addAll(matchSellOrder(order));
    } else {
      throw new IllegalArgumentException("Invalid order type");
    }

    return trades;
  }

  private List<Trade> matchBuyOrder(OrderRequest buyOrder) {
    List<Trade> trades = new ArrayList<>();

    while (!sellOrders.isEmpty()
        && buyOrder.notionalAmount > 0
        && buyOrder.price >= sellOrders.peek().price) {
      OrderRequest sellOrder = sellOrders.poll();
      trades.add(executeTrade(buyOrder, sellOrder));

      if (sellOrder.notionalAmount > 0) {
        sellOrders.offer(sellOrder);
      } else {
        processFullyFulfilledOrder(sellOrder);
      }

      if (buyOrder.notionalAmount == 0) {
        processFullyFulfilledOrder(buyOrder);
        break;
      }
    }

    if (buyOrder.notionalAmount > 0) {
      buyOrders.offer(buyOrder);
    }

    return trades;
  }

  private List<Trade> matchSellOrder(OrderRequest sellOrder) {
    List<Trade> trades = new ArrayList<>();

    while (!buyOrders.isEmpty()
        && sellOrder.notionalAmount > 0
        && sellOrder.price <= buyOrders.peek().price) {
      OrderRequest buyOrder = buyOrders.poll();
      trades.add(executeTrade(buyOrder, sellOrder));

      if (buyOrder.notionalAmount > 0) {
        buyOrders.offer(buyOrder);
      } else {
        processFullyFulfilledOrder(buyOrder);
      }

      if (sellOrder.notionalAmount == 0) {
        processFullyFulfilledOrder(sellOrder);
        break;
      }
    }

    if (sellOrder.notionalAmount > 0) {
      sellOrders.offer(sellOrder);
    }

    return trades;
  }

  private Trade executeTrade(OrderRequest buyOrder, OrderRequest sellOrder) {
    long tradePrice = sellOrder.price; // Assuming price-time priority
    int tradeAmount = Math.min(buyOrder.notionalAmount, sellOrder.notionalAmount);

    buyOrder.notionalAmount -= tradeAmount;
    sellOrder.notionalAmount -= tradeAmount;

    List<Integer> testOriginalAmount =
        Arrays.asList(buyOrder.getOriginalNotionalAmount(), sellOrder.getOriginalNotionalAmount());
    System.out.println("Original amounts: " + testOriginalAmount);

    return new Trade(
        buyOrder.id,
        sellOrder.id,
        tradeAmount,
        tradePrice,
        buyOrder.getOriginalNotionalAmount(),
        sellOrder.getOriginalNotionalAmount());
  }

  private void processFullyFulfilledOrder(OrderRequest order) {
    // Here you can add logic to process the fully fulfilled order
    // For example, you can save it to a database or perform any other necessary operations
    System.out.println("Order fully fulfilled and removed from order book: " + order.id);
    // Add your database processing logic here
    lastTenFulfilledOrders.offer(order);
    // If the queue size exceeds 10, remove the oldest element
    if (lastTenFulfilledOrders.size() > 10) {
      lastTenFulfilledOrders.poll();
    }
  }

  public OrderBookSummary getOrderBookSummary() {
    List<OrderBookSummary.OrderSummary> topBuys = new ArrayList<>();
    List<OrderBookSummary.OrderSummary> lowestSells = new ArrayList<>();

    // Get top 5 buy orders
    PriorityQueue<OrderRequest> tempBuyOrders = new PriorityQueue<>(buyOrders);
    for (int i = 0; i < 5 && !tempBuyOrders.isEmpty(); i++) {
      OrderRequest order = tempBuyOrders.poll();
      topBuys.add(
          new OrderBookSummary.OrderSummary(
              order.getPrice(), order.getNotionalAmount(), order.getOriginalNotionalAmount()));
    }

    // Get lowest 5 sell orders
    PriorityQueue<OrderRequest> tempSellOrders = new PriorityQueue<>(sellOrders);
    for (int i = 0; i < 5 && !tempSellOrders.isEmpty(); i++) {
      OrderRequest order = tempSellOrders.poll();
      lowestSells.add(
          new OrderBookSummary.OrderSummary(
              order.getPrice(), order.getNotionalAmount(), order.getOriginalNotionalAmount()));
    }

    return new OrderBookSummary(topBuys, lowestSells, symbol, lastTenFulfilledOrders);
  }
}
