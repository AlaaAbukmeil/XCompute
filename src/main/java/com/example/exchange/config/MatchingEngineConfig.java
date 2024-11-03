/* (C)2024 */
package com.example.exchange.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Configuration;

import com.example.exchange.jni.MatchingEngineJNI;
import com.example.exchange.service.KafkaProducer;

import jakarta.annotation.PreDestroy;

@Configuration
public class MatchingEngineConfig {

  private final Map<String, Long> matchingEngines = new ConcurrentHashMap<>();
  private final KafkaProducer kafkaProducerService;
  private final MatchingEngineJNI matchingEngineJNI;

  public MatchingEngineConfig(
      KafkaProducer kafkaProducerService, MatchingEngineJNI matchingEngineJNI) {
    this.kafkaProducerService = kafkaProducerService;
    this.matchingEngineJNI = matchingEngineJNI;
    initializeMatchingEngines();
  }

  private void initializeMatchingEngines() {
    String[] symbols = {"AAPL", "GOOGL", "MSFT", "AMZN", "FB"};
    for (String symbol : symbols) {
      long pointer = matchingEngineJNI.createMatchingEngine(symbol);
      matchingEngines.put(symbol, pointer);
    }
  }

  public Long getMatchingEnginePointer(String symbol) {
    return matchingEngines.get(symbol);
  }

  public Map<String, Long> getMatchingEngine() {
    return matchingEngines;
  }

  @PreDestroy
  public void cleanup() {
    for (Long pointer : matchingEngines.values()) {
      try {
        matchingEngineJNI.deleteMatchingEngine(pointer);
      } catch (Exception e) {
        // Log error but continue cleanup
      }
    }
    matchingEngines.clear();
  }
}
