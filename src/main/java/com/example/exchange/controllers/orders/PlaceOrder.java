/* (C)2024 */
package com.example.exchange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.exchange.annotation.RequiresAuth;
import com.example.exchange.config.OrderBookConfig;
import com.example.exchange.db.RepositoryExample;
import com.example.exchange.model.OrderRequest;
import com.example.exchange.service.KafkaProducer;
import com.example.exchange.service.OrderService;
import com.example.exchange.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class PlaceOrder {
  private final RepositoryExample repositoryExample;
  private final ObjectMapper objectMapper;
  private final JwtUtil jwtUtil;
  private final KafkaProducer kafkaProducer;
  private final OrderBookConfig orderBookConfig;
  private final OrderService orderService;

  @Autowired
  public PlaceOrder(
      RepositoryExample repositoryExample,
      ObjectMapper objectMapper,
      JwtUtil jwtUtil,
      KafkaProducer kafkaProducer,
      OrderBookConfig orderBookConfig,
      OrderService orderService) {
    this.repositoryExample = repositoryExample;
    this.orderService = orderService;
    this.objectMapper = objectMapper;
    this.jwtUtil = jwtUtil;
    this.kafkaProducer = kafkaProducer;
    this.orderBookConfig = orderBookConfig;
  }

  // @GetMapping("/users")
  // public List<Map<String, Object>> getAllUsers() {
  //   return repositoryExample.queryForList("SELECT * FROM users;");
  // }

  @GetMapping("/ping")
  public ResponseEntity<String> testing() {
    return ResponseEntity.ok("Hello");
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
}
