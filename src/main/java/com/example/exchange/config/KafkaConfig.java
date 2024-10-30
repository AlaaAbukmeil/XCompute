/* (C)2024 */
package com.example.exchange.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
  @Bean
  public NewTopic topicOrder() {
    return TopicBuilder.name("orders").partitions(10).replicas(3).build();
  }
}
