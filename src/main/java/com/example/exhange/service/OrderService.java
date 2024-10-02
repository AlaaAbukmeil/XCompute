package com.example.exchange.service;

import java.util.Random;
import java.util.UUID;
import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.exchange.model.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class OrderService {
    private final KafkaProducer producerService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_KEY_PREFIX = "order:";
    private static final Duration ORDER_EXPIRATION = Duration.ofHours(24);

    public OrderService(KafkaProducer producerService, RedisTemplate<String, String> redisTemplate) {
        this.producerService = producerService;
        this.redisTemplate = redisTemplate;
    }

    public void generateAndSendOrders(int count) {
        for (int i = 0; i < count; i++) {
            OrderRequest order = new OrderRequest(
                OrderRequest.OrderType.BUY,
                1 + new Random().nextInt(100),
                "ORD-" + UUID.randomUUID().toString()
            );

            String redisKey = REDIS_KEY_PREFIX + order.getOrderId();

            Boolean isNewOrder = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", ORDER_EXPIRATION);

            if (Boolean.TRUE.equals(isNewOrder)) {
                try {
                    producerService.sendOrder("orders", order);
                    System.out.println("Sent order: " + order.getOrderId());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Duplicate order detected: " + order.getOrderId());
            }
        }
    }
}