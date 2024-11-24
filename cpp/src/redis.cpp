// src/RedisClient.cpp
#include "redis.h"
#include <iostream>

std::unique_ptr<RedisClient> RedisClient::instance = nullptr;
std::mutex RedisClient::mutex;

RedisClient::RedisClient()
{
    try
    {
        // Configure Redis connection options
        sw::redis::ConnectionOptions connection_options;
        connection_options.host = "127.0.0.1"; // Redis host
        connection_options.port = 6379;        // Redis port
        // connection_options.password = "your_password";  // If needed

        // Configure connection pool options
        sw::redis::ConnectionPoolOptions pool_options;
        pool_options.size = 3;                                       // Pool size
        pool_options.wait_timeout = std::chrono::milliseconds(100);  // Wait timeout
        pool_options.connection_lifetime = std::chrono::minutes(10); // Connection lifetime

        // Create Redis client
        redis = std::make_unique<sw::redis::Redis>(connection_options, pool_options);

        // Test connection
        if (!ping())
        {
            throw std::runtime_error("Failed to connect to Redis server");
        }
    }
    catch (const sw::redis::Error &e)
    {
        std::cerr << "Redis client initialization failed: " << e.what() << std::endl;
        throw;
    }
}

RedisClient *RedisClient::getInstance()
{
    if (instance == nullptr)
    {
        std::lock_guard<std::mutex> lock(mutex);
        if (instance == nullptr)
        {
            instance = std::unique_ptr<RedisClient>(new RedisClient());
        }
    }
    return instance.get();
}

void RedisClient::lpush(const std::string &key, const std::string &value)
{
    try
    {
        redis->lpush(key, value);
    }
    catch (const sw::redis::Error &e)
    {
        std::cerr << "Redis lpush failed: " << e.what() << std::endl;
        throw;
    }
}

std::string RedisClient::rpop(const std::string &key)
{
    try
    {
        auto value = redis->rpop(key);
        return value ? *value : "";
    }
    catch (const sw::redis::Error &e)
    {
        std::cerr << "Redis rpop failed: " << e.what() << std::endl;
        throw;
    }
}

void RedisClient::ltrim(const std::string &key, long start, long stop)
{
    try
    {
        redis->ltrim(key, start, stop);
    }
    catch (const sw::redis::Error &e)
    {
        std::cerr << "Redis ltrim failed: " << e.what() << std::endl;
        throw;
    }
}

bool RedisClient::ping()
{
    try
    {
        return redis->ping() == "PONG";
    }
    catch (const sw::redis::Error &e)
    {
        std::cerr << "Redis ping failed: " << e.what() << std::endl;
        return false;
    }
}

bool RedisClient::get(const std::string &key, std::string &value)
{
    try
    {
        auto optional_value = redis->get(key);
        if (optional_value)
        {
            value = *optional_value;
            return true;
        }
        return false;
    }
    catch (const sw::redis::Error &e)
    {
        std::cerr << "Redis get failed: " << e.what() << std::endl;
        throw;
    }
}

void RedisClient::setex(const std::string &key, int seconds, const std::string &value)
{
    try
    {
        redis->setex(key, seconds, value);
    }
    catch (const sw::redis::Error &e)
    {
        std::cerr << "Redis setex failed: " << e.what() << std::endl;
        throw;
    }
}
bool RedisClient::set(const std::string &key, const std::string &value)
{
    redis->set(key, value);
    return true;
}