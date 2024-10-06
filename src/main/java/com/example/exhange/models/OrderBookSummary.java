/* (C)2024 */
package com.example.exchange.model;

import java.util.List;

public class OrderBookSummary {
  public List<OrderSummary> topBuys;
  public List<OrderSummary> lowestSells;

  public OrderBookSummary(List<OrderSummary> topBuys, List<OrderSummary> lowestSells) {
    this.topBuys = topBuys;
    this.lowestSells = lowestSells;
  }

  public static class OrderSummary {
    public long price;
    public int amount;
    public long notional;

    public OrderSummary(long price, int amount, long notional) {
      this.price = price;
      this.amount = amount;
      this.notional = notional;
    }
  }
}
