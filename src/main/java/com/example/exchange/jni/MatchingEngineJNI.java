/* (C)2024 */
package com.example.exchange.jni;

public class MatchingEngineJNI {
  static {
    try {
      System.loadLibrary("matching_engine");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
      System.exit(1);
    }
  }

  // Native methods
  public native void initializeEngine(String symbol);

  public native void addOrder(String orderId, long price, int quantity, boolean isBuy);

  public native Trade[] matchOrders();

  public native OrderBookSummary getOrderBookSummary();

  // Java representation of C++ structures
  public static class Trade {
    public String buyOrderId;
    public String sellOrderId;
    public int amount;
    public long price;
    public int originalBuyAmount;
    public int originalSellAmount;

    // Constructor and getters/setters
  }

  public static class OrderBookSummary {
    public OrderSummary[] topBuys;
    public OrderSummary[] lowestSells;
    public String symbol;
    public String[] lastTenFulfilledOrders;

    // Constructor and getters/setters

    public static class OrderSummary {
      public long price;
      public int quantity;
      public int originalQuantity;

      // Constructor and getters/setters
    }
  }
}
