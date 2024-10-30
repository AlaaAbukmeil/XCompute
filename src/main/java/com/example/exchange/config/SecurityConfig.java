/* (C)2024 */
package com.example.exchange.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.exchange.util.PasswordHasher;

@Configuration
public class SecurityConfig {

  @Value("${password.hash.secret}")
  private String secret;

  @Bean
  public PasswordHasher passwordHasher() {
    return new PasswordHasher(secret);
  }
}
