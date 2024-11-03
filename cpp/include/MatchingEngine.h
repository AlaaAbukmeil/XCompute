#ifndef MATCHING_ENGINE_H
#define MATCHING_ENGINE_H

#include <string>
#include <vector>
#include <iostream>
#include <queue>
#include <stdexcept>
#include <sstream>

using namespace std;

class OrderRequest
{
public:
    string type;
    int notionalAmount;
    int originalNotionalAmount;
    string id;
    int price;
    string symbol;

    OrderRequest() : type("BUY"),
                     notionalAmount(0),
                     originalNotionalAmount(0),
                     id(""),
                     price(0),
                     symbol("") {}

    OrderRequest(string t, int amount, int originalNotionalAmount, string orderId, int p, string sym) : type(t == "BUY" ? "BUY" : "SELL"),
                                                                                                        notionalAmount(amount),
                                                                                                        originalNotionalAmount(originalNotionalAmount),
                                                                                                        id(orderId),
                                                                                                        price(p),
                                                                                                        symbol(sym)
    {
    }

    const int &getPrice() const { return price; }
    const string &getId() const { return id; }
    const string &getSymbol() const { return symbol; }
    int getNotionalAmount() const { return notionalAmount; }
    int getOriginalNotionalAmount() const { return originalNotionalAmount; }
    string getType() const { return type; }
    string print() const
    {
        std::stringstream ss;
        ss << "Order Details: Type: " << (type == "BUY" ? "BUY" : "SELL")
           << " ID: " << id
           << " Price: " << price
           << " Amount: " << notionalAmount
           << " Original Amount: " << originalNotionalAmount
           << " Symbol: " << symbol;
        return ss.str();
    }
};

class Trade
{
private:
    string buyOrderId;
    string sellOrderId;
    int amount;
    long price;
    int originalBuyAmount;
    int originalSellAmount;

public:
    Trade() : buyOrderId(""),
              sellOrderId(""),
              amount(0),
              price(0),
              originalBuyAmount(0),
              originalSellAmount(0) {}

    Trade(const string &buyId, const string &sellId,
          int tradeAmount, long tradePrice,
          int origBuyAmt, int origSellAmt) : buyOrderId(buyId),
                                             sellOrderId(sellId),
                                             amount(tradeAmount),
                                             price(tradePrice),
                                             originalBuyAmount(origBuyAmt),
                                             originalSellAmount(origSellAmt) {}

    Trade(const OrderRequest &buyOrder, const OrderRequest &sellOrder, int tradeAmount) : buyOrderId(buyOrder.getId()),
                                                                                          sellOrderId(sellOrder.getId()),
                                                                                          amount(tradeAmount),
                                                                                          price(sellOrder.getPrice()), // or buyOrder.getPrice()
                                                                                          originalBuyAmount(buyOrder.getOriginalNotionalAmount()),
                                                                                          originalSellAmount(sellOrder.getOriginalNotionalAmount())
    {
    }

    const string &getBuyOrderId() const { return buyOrderId; }
    const string &getSellOrderId() const { return sellOrderId; }
    int getAmount() const { return amount; }
    long getPrice() const { return price; }
    int getOriginalBuyAmount() const { return originalBuyAmount; }
    int getOriginalSellAmount() const { return originalSellAmount; }

    string toString() const
    {
        return "Trade{buyOrderId=" + buyOrderId +
               ", sellOrderId=" + sellOrderId +
               ", amount=" + to_string(amount) +
               ", price=" + to_string(price) +
               ", originalBuyAmount=" + to_string(originalBuyAmount) +
               ", originalSellAmount=" + to_string(originalSellAmount) + "}";
    }
};

class BuyOrderCompare
{
public:
    bool operator()(const OrderRequest &a, const OrderRequest &b) const
    {
        if (a.getPrice() != b.getPrice())
        {
            return a.getPrice() < b.getPrice();
        }
        return a.getId() > b.getId();
    }
};

class SellOrderCompare
{
public:
    bool operator()(const OrderRequest &a, const OrderRequest &b) const
    {
        if (a.getPrice() != b.getPrice())
        {
            return a.getPrice() > b.getPrice();
        }
        return a.getId() > b.getId();
    }
};

class OrderBookSummary
{
public:
    class OrderSummary
    {
    public:
        long price;
        int notional;
        int originalAmount;
        string id;

        OrderSummary(long a = 0, int b = 0, int c = 0, string d = "") : price(a), notional(b), originalAmount(c), id(d) {}
    };
    OrderBookSummary() {};
    OrderBookSummary(const vector<OrderSummary> &buys,
                     const vector<OrderSummary> &sells,
                     const string &sym,
                     const deque<OrderRequest> &orders)
        : topBuys(buys), lowestSells(sells), symbol(sym), lastTenFulfilledOrders(orders) {}
    vector<OrderSummary> topBuys;
    vector<OrderSummary> lowestSells;
    string symbol;
    deque<OrderRequest> lastTenFulfilledOrders;
    string toCompactString() const
    {
        stringstream ss;
        ss << "Symbol: " << symbol << "\n";

        // Buy trades section
        ss << "Top Buys:\n";
        if (topBuys.empty())
        {
            ss << "  No buy trades\n";
        }
        else
        {
            for (const auto &trade : topBuys)
            {
                ss << "  Buy #" << trade.id
                   << "\n    Notional: " << trade.notional
                   << "\n    Price: " << trade.price
                   << "\n";
            }
        }

        // Sell trades section
        ss << "\nLowest Sells:\n";
        if (lowestSells.empty())
        {
            ss << "  No sell trades\n";
        }
        else
        {
            for (const auto &trade : lowestSells)
            {
                ss << "  Sell #" << trade.id
                   << "\n    Notional: " << trade.notional
                   << "\n    Price: " << trade.price
                   << "\n";
            }
        }
        if (!lastTenFulfilledOrders.empty())
        {
            for (const auto &trade : lastTenFulfilledOrders)
            {
                ss << "  Completed #" << trade.id
                   << "\n    Notional: " << trade.notionalAmount
                   << "\n    Price: " << trade.price
                   << "\n    Type: " << trade.type
                   << "\n";
            }
        }

        return ss.str();
    }
};

class MatchingEngine
{
public:
    string symbol;
    MatchingEngine(const string &bookName) : symbol(bookName),
                                             buyOrders(),
                                             sellOrders() {}

    string printHello();
    vector<Trade> insertOrder(OrderRequest &order);
    OrderBookSummary getMatchingEngineSummary();
    string getSymbol()
    {
        return symbol;
    };

private:
    priority_queue<OrderRequest, vector<OrderRequest>, BuyOrderCompare> buyOrders;
    priority_queue<OrderRequest, vector<OrderRequest>, SellOrderCompare> sellOrders;
    deque<OrderRequest> lastTenFulfilledOrders;
    const size_t MAX_SIZE = 10;
    vector<Trade> matchBuyOrder(OrderRequest& buyOrder);
    vector<Trade> matchSellOrder(OrderRequest& sellOrder);
    Trade executeTrade(OrderRequest& buyOrder, OrderRequest& sellOrder);
    void processFullyFulfilledOrder(OrderRequest order);
};

#endif // MATCHING_ENGINE_H