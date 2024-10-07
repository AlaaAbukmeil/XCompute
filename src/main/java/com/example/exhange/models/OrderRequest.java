/* (C)2024 */
package com.example.exchange.model;

public class OrderRequest {
  public enum OrderType {
    BUY,
    SELL
  }

  public OrderType type;
  public int notionalAmount;
  public int originalNotionalAmount;
  public String id;
  public long price;
  public String symbol;

  public OrderRequest() {
    this.originalNotionalAmount = 0;
  }

  // Constructor with fields
  public OrderRequest(OrderType type, int notionalAmount, String id, long price, String symbol) {
    this.type = type;
    this.notionalAmount = notionalAmount;
    this.id = id;
    this.price = price;
    this.symbol = symbol;
    this.originalNotionalAmount = notionalAmount;
  }

  public int getNotionalAmount() {
    return notionalAmount;
  }

  public int getOriginalNotionalAmount() {
    return originalNotionalAmount;
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
        + ", originalNotionalAmount="
        + originalNotionalAmount
        + ", price="
        + price
        + '}';
  }
}
