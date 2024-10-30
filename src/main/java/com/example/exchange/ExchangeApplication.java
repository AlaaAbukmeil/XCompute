/* (C)2024 */
package com.example.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.exchange")
public class ExchangeApplication {

  public static void main(String[] args) {
    SpringApplication.run(ExchangeApplication.class, args);
  }
}
