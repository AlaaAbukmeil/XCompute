/* (C)2024 */
package com.example.exchange.model;

import java.math.BigDecimal;

public class OrderRequest {
  public enum OrderType {
    BUY,
    SELL
  }

  public OrderType type;
  public BigDecimal notionalAmount;

  @Override
  public String toString() {
    return "OrderRequest{" + "type=" + type + ", notionalAmount=" + notionalAmount + '}';
  }
}
