/* (C)2024 */
package com.example.exchange.model;

public class OrderRequest {

  public String type;
  public int notionalAmount;
  public int originalNotionalAmount;
  public String id;
  public int price;
  public String symbol;

  public OrderRequest() {
    this.originalNotionalAmount = 0;
  }

  // Constructor with fields
  public OrderRequest(String type, int notionalAmount, String id, int price, String symbol) {
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

  public int getPrice() {
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
