/* (C)2024 */
package com.example.exchange.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.exchange.annotation.RequiresAuth;
import com.example.exchange.config.OrderBookConfig;
import com.example.exchange.model.OrderBookSummary;
import com.example.exchange.model.OrderRequest;
import com.example.exchange.repo.RepositoryExample;
import com.example.exchange.service.KafkaProducer;
import com.example.exchange.service.OrderBook;
import com.example.exchange.service.OrderService;
import com.example.exchange.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class PlaceOrder {
  private final RepositoryExample repositoryExample;
  private final OrderService orderService;
  private final ObjectMapper objectMapper;
  private final JwtUtil jwtUtil;
  private final KafkaProducer kafkaProducer;
  private final OrderBookConfig orderBookConfig;

  @Autowired
  public PlaceOrder(
      RepositoryExample repositoryExample,
      OrderService orderService,
      ObjectMapper objectMapper,
      JwtUtil jwtUtil,
      KafkaProducer kafkaProducer,
      OrderBookConfig orderBookConfig) {
    this.repositoryExample = repositoryExample;
    this.orderService = orderService;
    this.objectMapper = objectMapper;
    this.jwtUtil = jwtUtil;
    this.kafkaProducer = kafkaProducer;
    this.orderBookConfig = orderBookConfig;
  }

  @GetMapping("/users")
  public List<Map<String, Object>> getAllUsers() {
    return repositoryExample.queryForList("SELECT * FROM users;");
  }

  @GetMapping("/")
  public ResponseEntity<String> testing() {
    return ResponseEntity.ok("Hello");
  }

  @PostMapping("/generate")
  @RequiresAuth
  public ResponseEntity<String> generateOrders(@RequestParam(defaultValue = "1000") int count) {
    orderService.generateThousandTrades();
    return ResponseEntity.ok("Done");
  }

  @PostMapping("/submit-order")
  @RequiresAuth
  public ResponseEntity<String> submitOrder(@RequestBody OrderRequest orderRequest) {
    try {
      kafkaProducer.sendOrder("orders", orderRequest);
      return ResponseEntity.ok("Done");
    } catch (JsonProcessingException e) {
      return ResponseEntity.internalServerError().body("Error submitting order");
    }
  }

  @PostMapping("/print-order-books")
  @RequiresAuth
  public ResponseEntity<String> submitOrder() {
    try {
      kafkaProducer.printOrders("print-order-books");
      return ResponseEntity.ok("Done");
    } catch (JsonProcessingException e) {
      return ResponseEntity.internalServerError().body("Error processing JSON");
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error submitting order: " + e.getMessage());
    }
  }

  @GetMapping("/book")
  @RequiresAuth
  public ResponseEntity<String> getPrices(@RequestParam(defaultValue = "AAPL") String symbol) {
    try {
      OrderBook orderBook = orderBookConfig.getOrderBook(symbol);
      String jsonOrderBook = objectMapper.writeValueAsString(orderBook);
      return ResponseEntity.ok(jsonOrderBook);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error submitting order");
    }
  }

  @GetMapping("/books")
  @RequiresAuth
  public ResponseEntity<String> getAllBooksSummary() {
    String[] symbols = {"AAPL", "GOOGL", "MSFT", "AMZN", "FB"};
    Map<String, OrderBookSummary> allBooks = new HashMap<>();

    try {
      for (String symbol : symbols) {
        OrderBook orderBook = orderBookConfig.getOrderBook(symbol);
        OrderBookSummary orderBookSummary = orderBook.getOrderBookSummary();
        allBooks.put(symbol, orderBookSummary);
      }

      String jsonAllBooks = objectMapper.writeValueAsString(allBooks);
      return ResponseEntity.ok(jsonAllBooks);
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("Error retrieving order books: " + e.getMessage());
    }
  }
}
