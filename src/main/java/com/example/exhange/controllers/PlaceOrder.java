/* (C)2024 */
package com.example.exchange.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.exchange.model.OrderRequest;
import com.example.exchange.model.User;
import com.example.exchange.repo.RepositoryExample;

@RestController
public class PlaceOrder {
  private final RepositoryExample repositoryExample;

  @Autowired
  public PlaceOrder(RepositoryExample repositoryExample) {
    this.repositoryExample = repositoryExample;
  }

  @PostMapping("/order")
  public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
    // Your order processing logic here
    System.out.println("Received order: " + orderRequest);
    User testUser = new User("id", "email", "password");

    return ResponseEntity.ok(testUser.toString());
  }

  @GetMapping("/users")
  public List<Map<String, Object>> getAllUsers() {
    return repositoryExample.queryForList("SELECT * FROM users;");
  }

  @GetMapping("/")
  public ResponseEntity<String> testing() {
    return ResponseEntity.ok("Hello");
  }
}
