/* (C)2024 */
package com.example.exchange.model;

import java.util.List;
import java.util.Queue;

public class OrderBookSummary {
  public List<OrderSummary> topBuys;
  public List<OrderSummary> lowestSells;
  public String symbol;
  public Queue<OrderRequest> lastTenFulfilledOrders;

  public OrderBookSummary(
      List<OrderSummary> topBuys,
      List<OrderSummary> lowestSells,
      String symbol,
      Queue<OrderRequest> lastTenFulfilledOrders) {
    this.topBuys = topBuys;
    this.lowestSells = lowestSells;
    this.symbol = symbol;
    this.lastTenFulfilledOrders = lastTenFulfilledOrders;
  }

  public String getSymbol() {
    return symbol;
  }

  public static class OrderSummary {
    public long price;
    public int notional;
    public int originalAmount;

    public OrderSummary(long price, int notional, int originalAmount) {
      this.price = price;
      this.notional = notional;
      this.originalAmount = originalAmount;
    }
  }
}
