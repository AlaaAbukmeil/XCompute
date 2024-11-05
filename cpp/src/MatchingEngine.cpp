#include "MatchingEngine.h"
#include "utils.h"

string MatchingEngine::printHello()
{
    return "Hello from C++, 2!";
}

vector<Trade> MatchingEngine::insertOrder(OrderRequest &order)
{
    vector<Trade> trades;
    if (order.type == "BUY")
    {
        vector<Trade> buyTrades = matchBuyOrder(order);
        for (auto &&trade : buyTrades)
        {
            trades.emplace_back(move(trade));
        }
    }
    else if (order.type == "SELL")
    {
        vector<Trade> sellTrades = matchSellOrder(order);
        for (auto &&trade : sellTrades)
        {
            trades.emplace_back(move(trade));
        }
    }
    else
    {
        throw invalid_argument("Invalid order type");
    }

    return trades;
}

vector<Trade> MatchingEngine::matchBuyOrder(OrderRequest &buyOrder)
{
    vector<Trade> trades;
    while (!sellOrders.empty() && buyOrder.notionalAmount > 0 && buyOrder.getPrice() >= sellOrders.top().getPrice())
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

vector<Trade> MatchingEngine::matchSellOrder(OrderRequest &sellOrder)
{
    vector<Trade> trades;
    while (!buyOrders.empty() && sellOrder.notionalAmount > 0 && sellOrder.getPrice() <= buyOrders.top().getPrice())
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

Trade MatchingEngine::executeTrade(OrderRequest &buyOrder, OrderRequest &sellOrder)
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

void MatchingEngine::processFullyFulfilledOrder(OrderRequest& order)
{
    // logToFile("fulfiled order id" + order.getId() + " with original notional: "+to_string(order.getOriginalNotionalAmount()));

    if (lastTenFulfilledOrders.size() >= 10)
    {
        lastTenFulfilledOrders.pop_front();
    }
    lastTenFulfilledOrders.emplace_back(std::move(order));
}

OrderBookSummary MatchingEngine::getMatchingEngineSummary()
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
            order.getOriginalNotionalAmount(),
            order.getId());
        tempBuyOrders.pop();
    }

    auto tempSellOrders = sellOrders; 
    for (int i = 0; i < 5 && !tempSellOrders.empty(); i++)
    {
        const OrderRequest &order = tempSellOrders.top();
        summary.lowestSells.emplace_back(
            order.getPrice(),
            order.getNotionalAmount(),
            order.getOriginalNotionalAmount(), order.getId());
        tempSellOrders.pop();
    }

    return summary;
}