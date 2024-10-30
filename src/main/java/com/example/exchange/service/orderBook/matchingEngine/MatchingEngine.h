#ifndef MATCHING_ENGINE_H
#define MATCHING_ENGINE_H

#include <jni.h>
#include <string>
#include <vector>
#include <queue>
#include <unordered_map>

class OrderRequest {
public:
    enum class OrderType { BUY, SELL };

    std::string id;
    long price;
    int notionalAmount;
    int originalNotionalAmount;
    OrderType type;
    std::string symbol;

    OrderRequest(const std::string& id, long price, int notionalAmount, OrderType type, const std::string& symbol)
        : id(id), price(price), notionalAmount(notionalAmount), originalNotionalAmount(notionalAmount), type(type), symbol(symbol) {}

    bool isBuy() const { return type == OrderType::BUY; }
};

class Trade {
public:
    std::string buyOrderId;
    std::string sellOrderId;
    int amount;
    long price;
    int originalBuyAmount;
    int originalSellAmount;

    Trade(const std::string& buyOrderId, const std::string& sellOrderId, int amount, long price, int originalBuyAmount, int originalSellAmount)
        : buyOrderId(buyOrderId), sellOrderId(sellOrderId), amount(amount), price(price), originalBuyAmount(originalBuyAmount), originalSellAmount(originalSellAmount) {}
};

class OrderBookSummary {
public:
    struct OrderSummary {
        long price;
        int quantity;
        int originalQuantity;

        OrderSummary(long p, int q, int oq) : price(p), quantity(q), originalQuantity(oq) {}
    };

    std::vector<OrderSummary> topBuys;
    std::vector<OrderSummary> lowestSells;
    std::string symbol;
    std::vector<std::string> lastTenFulfilledOrders;
};

class MatchingEngine {
private:
    std::string symbol;
    std::priority_queue<OrderRequest> buyOrders;
    std::priority_queue<OrderRequest> sellOrders;
    std::queue<std::string> lastTenFulfilledOrders;

    std::vector<Trade> matchBuyOrder(OrderRequest& buyOrder);
    std::vector<Trade> matchSellOrder(OrderRequest& sellOrder);
    Trade executeTrade(OrderRequest& buyOrder, OrderRequest& sellOrder);
    void processFullyFulfilledOrder(const OrderRequest& order);

public:
    MatchingEngine(const std::string& symbol);
    void addOrder(const std::string& orderId, long price, int quantity, bool isBuy);
    std::vector<Trade> matchOrders();
    OrderBookSummary getOrderBookSummary();
};

#endif // MATCHING_ENGINE_H