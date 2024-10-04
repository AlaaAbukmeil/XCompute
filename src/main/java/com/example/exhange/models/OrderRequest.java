/* (C)2024 */
package com.example.exchange.model;

public class OrderRequest {
  public enum OrderType {
    BUY,
    SELL
  }

  public OrderType type;
  public int notionalAmount;
  public String id;
  public long price;
  public String symbol;

  public OrderRequest() {}

  // Constructor with fields
  public OrderRequest(OrderType type, int notionalAmount, String id, long price, String symbol) {
    this.type = type;
    this.notionalAmount = notionalAmount;
    this.id = id;
    this.price = price;
    this.symbol = symbol;
  }

  public long getPrice() {
    return price;
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "OrderRequest{"
        + "type="
        + type
        + ", notionalAmount="
        + notionalAmount
        + ", price="
        + price
        + '}';
  }
}
