/* (C)2024 */
package com.example.exchange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exchange.jni.MatchingEngineJNI;

@RestController
public class MatchingEngine {

  private final MatchingEngineJNI matchingEngineJNI;

  @Autowired
  public MatchingEngine(MatchingEngineJNI matchingEngineJNI) {

    this.matchingEngineJNI = matchingEngineJNI;
  }

  @GetMapping("/matching-engine")
  public ResponseEntity<String> testMatchingEngine() {
    return ResponseEntity.ok(matchingEngineJNI.printHello());
  }
}
