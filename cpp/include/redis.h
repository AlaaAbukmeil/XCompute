// include/RedisClient.h
#pragma once

#include <sw/redis++/redis++.h>
#include <memory>
#include <mutex>
#include <string>
#include <fmt/format.h>
#include <chrono>
#include <algorithm>
#include <iomanip>
#include <sstream>
#include <ctime>

class RedisClient
{
private:
    static std::unique_ptr<RedisClient> instance;
    static std::mutex mutex;
    int MAX_TRADES_RETAINED = 1000;

    std::unique_ptr<sw::redis::Redis> redis;

    // Private constructor for singleton
    RedisClient();

public:
    // Delete copy constructor and assignment operator
    RedisClient(const RedisClient &) = delete;
    RedisClient &operator=(const RedisClient &) = delete;

    static RedisClient *getInstance();

    // Redis operations
    void lpush(const std::string &key, const std::string &value);
    std::string rpop(const std::string &key);
    void ltrim(const std::string &key, long start, long stop);
    void pushTrade(const std::string &tradeJson)
    {
        lpush("fulfilled_trades", tradeJson);
        ltrim("fulfilled_trades", 0, MAX_TRADES_RETAINED - 1);
    }
    bool ping();

    // Getter for raw Redis client (use carefully)
    sw::redis::Redis *getRedis() { return redis.get(); }
    bool get(const std::string &key, std::string &value);
    void setex(const std::string &key, int seconds, const std::string &value);
};