/* (C)2024 */
package com.example.exchange.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

import com.example.exchange.model.OrderBookSummary;
import com.example.exchange.model.OrderRequest;

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

  public List<Trade> insertOrder(OrderRequest order) {
    List<Trade> trades = new ArrayList<>();

    if (order.type == OrderRequest.OrderType.BUY) {
      trades.addAll(matchBuyOrder(order));
    } else if (order.type == OrderRequest.OrderType.SELL) {
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

    return new Trade(buyOrder.id, sellOrder.id, tradeAmount, tradePrice);
  }

  private void processFullyFulfilledOrder(OrderRequest order) {
    // Here you can add logic to process the fully fulfilled order
    // For example, you can save it to a database or perform any other necessary operations
    System.out.println("Order fully fulfilled and removed from order book: " + order.id);
    // Add your database processing logic here
  }

  public OrderRequest getHighestBuyOrder() {
    return buyOrders.peek();
  }

  public OrderRequest getLowestSellOrder() {
    return sellOrders.peek();
  }

  public int getBuyOrdersSize() {
    return buyOrders.size();
  }

  public int getSellOrdersSize() {
    return sellOrders.size();
  }

  public long getCurrentPrice() {
    OrderRequest highestBuy = getHighestBuyOrder();
    OrderRequest lowestSell = getLowestSellOrder();

    if (highestBuy != null && lowestSell != null) {
      return (highestBuy.getPrice() + lowestSell.getPrice()) / 2;
    } else if (highestBuy != null) {
      return highestBuy.getPrice();
    } else if (lowestSell != null) {
      return lowestSell.getPrice();
    } else {
      throw new IllegalStateException("No orders in the order book");
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
              order.getPrice(),
              order.getNotionalAmount(),
              (long) order.getPrice() * order.getNotionalAmount()));
    }

    // Get lowest 5 sell orders
    PriorityQueue<OrderRequest> tempSellOrders = new PriorityQueue<>(sellOrders);
    for (int i = 0; i < 5 && !tempSellOrders.isEmpty(); i++) {
      OrderRequest order = tempSellOrders.poll();
      lowestSells.add(
          new OrderBookSummary.OrderSummary(
              order.getPrice(),
              order.getNotionalAmount(),
              (long) order.getPrice() * order.getNotionalAmount()));
    }

    return new OrderBookSummary(topBuys, lowestSells);
  }
}

class Trade {
  String buyOrderId;
  String sellOrderId;
  int amount;
  long price;

  public Trade(String buyOrderId, String sellOrderId, int amount, long price) {
    this.buyOrderId = buyOrderId;
    this.sellOrderId = sellOrderId;
    this.amount = amount;
    this.price = price;
  }
}
