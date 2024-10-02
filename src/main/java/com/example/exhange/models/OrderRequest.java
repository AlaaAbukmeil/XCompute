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

  public OrderRequest() {}

  // Constructor with fields
  public OrderRequest(OrderType type, int notionalAmount, String id) {
    this.type = type;
    this.notionalAmount = notionalAmount;
    this.id = id;
  }

  @Override
  public String toString() {
    return "OrderRequest{" + "type=" + type + ", notionalAmount=" + notionalAmount + '}';
  }
}
