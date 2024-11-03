/* (C)2024 */
package com.example.exchange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.exchange.config.MatchingEngineConfig;
import com.example.exchange.jni.MatchingEngineJNI;
import com.example.exchange.model.OrderRequest;

@RestController
public class MatchingEngine {

  private final MatchingEngineJNI matchingEngineJNI;
  private final MatchingEngineConfig matchingEngineConfig;

  @Autowired
  public MatchingEngine(
      MatchingEngineJNI matchingEngineJNI, MatchingEngineConfig matchingEngineConfig) {

    this.matchingEngineJNI = matchingEngineJNI;
    this.matchingEngineConfig = matchingEngineConfig;
  }

  @GetMapping("/matching-engine")
  public ResponseEntity<String> testMatchingEngine(@RequestBody OrderRequest orderRequest) {

    long pointer = matchingEngineConfig.getMatchingEnginePointer(orderRequest.symbol);
    System.out.printf("pointer: %d (0x%X)%n", pointer, pointer);

    String trades =
        matchingEngineJNI.insertOrder(
            pointer,
            orderRequest.id,
            orderRequest.type,
            orderRequest.price,
            orderRequest.notionalAmount,
            orderRequest.originalNotionalAmount);

    return ResponseEntity.ok(trades);
  }

  @GetMapping("/matching-engine-summary")
  public ResponseEntity<String> getSummary(@RequestBody OrderRequest orderRequest) {

    long pointer = matchingEngineConfig.getMatchingEnginePointer(orderRequest.symbol);

    String summary = matchingEngineJNI.getMatchingEngineSummary(pointer);

    return ResponseEntity.ok(summary);
  }
}
