/* (C)2024 */
package com.example.exchange.model;

public class Trade {
  String buyOrderId;
  String sellOrderId;
  int amount;
  long price;
  int originalBuyAmount;
  int originalSellAmount;

  public Trade(
      String buyOrderId,
      String sellOrderId,
      int amount,
      long price,
      int originalBuyAmount,
      int originalSellAmount) {
    this.buyOrderId = buyOrderId;
    this.sellOrderId = sellOrderId;
    this.amount = amount;
    this.price = price;
    this.originalBuyAmount = originalBuyAmount;
    this.originalSellAmount = originalSellAmount;
  }
}
