#include "MatchingEngine.h"
#include <string>

string MatchingEngine::printHello()
{
    return "Hello from C++, 2!";
}

vector<Trade> MatchingEngine::insertOrder(OrderRequest& order)
{
    vector<Trade> trades;
    if (order.type == OrderRequest::OrderType::BUY)
    {
        // sth
        vector<Trade> buyTrades;
        trades.insert(trades.end(), buyTrades.begin(), buyTrades.end());
    }
    else if (order.type == OrderRequest::OrderType::SELL)
    {
        // sth
        vector<Trade> sellTrades;
        trades.insert(trades.end(), sellTrades.begin(), sellTrades.end());
    }
    else
    {
        throw invalid_argument("Invalid order type");
    }

    return trades;
}

vector<Trade> MatchingEngine::matchBuyOrder(OrderRequest buyOrder)
{
    vector<Trade> trades;
    while (!sellOrders.empty() && buyOrder.getPrice() > sellOrders.top().getPrice())
    {
        OrderRequest sellOrder = sellOrders.top();
        sellOrders.pop();
        trades.push_back(executeTrade(buyOrder, sellOrder));

        if (sellOrder.getNotionalAmount() > 0)
        {
            sellOrders.push(sellOrder);
        }
        else
        {
            processFullyFulfilledOrder(sellOrder);
        }

        if (buyOrder.notionalAmount == 0)
        {
            processFullyFulfilledOrder(buyOrder);
            break;
        }
    };
    if (buyOrder.getNotionalAmount() > 0)
    {
        buyOrders.push(buyOrder);
    }
    return trades;
}

vector<Trade> MatchingEngine::matchSellOrder(OrderRequest sellOrder)
{
    vector<Trade> trades;
    while (!buyOrders.empty() && sellOrder.getPrice() < buyOrders.top().getPrice())
    {
        OrderRequest buyOrder = buyOrders.top();
        buyOrders.pop();
        trades.push_back(executeTrade(buyOrder, sellOrder));

        if (buyOrder.getNotionalAmount() > 0)
        {
            buyOrders.push(buyOrder);
        }
        else
        {
            processFullyFulfilledOrder(buyOrder);
        }

        if (sellOrder.getNotionalAmount() == 0)
        {
            processFullyFulfilledOrder(sellOrder);
            break;
        }
    };
    if (sellOrder.getNotionalAmount() > 0)
    {
        sellOrders.push(sellOrder);
    }

    return trades;
}

Trade MatchingEngine::executeTrade(OrderRequest buyOrder, OrderRequest sellOrder)
{
    long tradePrice = sellOrder.price;
    int tradeAmount = min(buyOrder.notionalAmount, sellOrder.notionalAmount);

    buyOrder.notionalAmount -= tradeAmount;
    sellOrder.notionalAmount -= tradeAmount;

    return Trade(
        buyOrder.id,
        sellOrder.id,
        tradeAmount,
        tradePrice,
        buyOrder.getOriginalNotionalAmount(),
        sellOrder.getOriginalNotionalAmount());
}

void MatchingEngine::processFullyFulfilledOrder(OrderRequest order)
{
    lastTenFulfilledOrders.push_back(order);
    if (lastTenFulfilledOrders.size() > 10)
    {
        lastTenFulfilledOrders.pop_front();
    }
}

OrderBookSummary MatchingEngine::getOrderBookSummary()
{
    OrderBookSummary summary;
    summary.symbol = symbol;
    summary.lastTenFulfilledOrders = lastTenFulfilledOrders;

    auto tempBuyOrders = buyOrders;
    for (int i = 0; i < 5 && !tempBuyOrders.empty(); i++)
    {
        const OrderRequest &order = tempBuyOrders.top();
        summary.topBuys.emplace_back(
            order.getPrice(),
            order.getNotionalAmount(),
            order.getOriginalNotionalAmount());
        tempBuyOrders.pop();
    }

    auto tempSellOrders = sellOrders; // Create a copy
    for (int i = 0; i < 5 && !tempSellOrders.empty(); i++)
    {
        const OrderRequest &order = tempSellOrders.top();
        summary.lowestSells.emplace_back(
            order.getPrice(),
            order.getNotionalAmount(),
            order.getOriginalNotionalAmount());
        tempSellOrders.pop();
    }

    return summary;
}