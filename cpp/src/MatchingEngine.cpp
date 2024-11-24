#include "MatchingEngine.h"
#include "utils.h"
#include "redis.h"

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

    auto now = std::chrono::system_clock::now();
    auto minute_timestamp = std::chrono::duration_cast<std::chrono::minutes>(
                                now.time_since_epoch())
                                .count() *
                            60000; // Convert to milliseconds but rounded to minute

    std::string minuteKey = fmt::format("candle:{}:{}", buyOrder.symbol, minute_timestamp);

    auto redis = RedisClient::getInstance();

    std::string existingCandle;
    bool exists = redis->get(minuteKey, existingCandle);

    double min_price, max_price;

    if (exists)
    {
        // Parse existing candle data
        try
        {
            auto candleData = nlohmann::json::parse(existingCandle);
            // Ensure both values are double for comparison
            double existing_min = candleData["min"].get<double>();
            double existing_max = candleData["max"].get<double>();
            double current_price = static_cast<double>(buyOrder.price);

            min_price = std::min<double>(existing_min, current_price);
            max_price = std::max<double>(existing_max, current_price);
        }
        catch (...)
        {
            min_price = max_price = static_cast<double>(buyOrder.price);
        }
    }
    else
    {
        // First trade for this minute
        min_price = max_price = static_cast<double>(buyOrder.price);
    }
    // Update candle data
    std::time_t time = minute_timestamp / 1000; // Convert from ms to seconds
    std::tm *tm = std::localtime(&time);
    std::ostringstream datetime_ss;
    datetime_ss << std::put_time(tm, "%d-%m-%Y %H:%M");
    std::string formatted_datetime = datetime_ss.str();

    std::string candleJson = fmt::format(
        R"({{"symbol":"{}","min":{},"max":{},"timestamp":{},"datetime":"{}"}})",
        buyOrder.symbol,
        min_price,
        max_price,
        minute_timestamp,
        formatted_datetime);

    // Store candle data with 1-hour expiration
    redis->setex(minuteKey, 3600, candleJson);

    // Store trade data as before
    std::string tradeJson = fmt::format(
        R"({{"id":"{}","symbol":"{}","price":{},"timestamp":{}}})",
        buyOrder.id,
        buyOrder.symbol,
        buyOrder.price,
        now.time_since_epoch().count());

    redis->pushTrade(tradeJson);

    return Trade(
        buyOrder.id,
        sellOrder.id,
        tradeAmount,
        tradePrice,
        buyOrder.getOriginalNotionalAmount(),
        sellOrder.getOriginalNotionalAmount());
}

void MatchingEngine::processFullyFulfilledOrder(OrderRequest &order)
{

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