package com.example.exchange.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.exchange.service.OrderBook;

@Configuration
public class OrderBookConfig {

    private Map<String, OrderBook> orderBooks;

    @Bean
    public Map<String, OrderBook> orderBooks() {
        if (orderBooks == null) {
            orderBooks = new HashMap<>();
            String[] symbols = {"AAPL", "GOOGL", "MSFT", "AMZN", "FB"};
            for (String symbol : symbols) {
                OrderBook orderBook = new OrderBook();
                orderBook.setSymbol(symbol);
                orderBooks.put(symbol, orderBook);
            }
        }
        return orderBooks;
    }
    
    public Map<String, OrderBook> getOrderBooks() {
        return orderBooks();
    }
}