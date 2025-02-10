package com.queue.worker.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/v1/rate-limit")
public class RateLimitController {

    private final StringRedisTemplate redisTemplate;

    @Value("${rate.limit.max-requests:1}")
    private int maxRequests;

    @Value("${rate.limit.time-window-seconds:10}")
    private int timeWindowSeconds;

    public RateLimitController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/external-service")
    public ResponseEntity<String> callExternalService() {

        String key = "rate-limit:external-service";
        boolean isAllowed = acquireRateLimitSlot(key);

        if (isAllowed) {
            // Simulate service call
            return ResponseEntity.ok("External service successfully called.");
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Please try again later.");
        }
    }

    private boolean acquireRateLimitSlot(String key) {
        // Increment request count for the given key
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == 1) {
            // Set expiry if this is the first request
            redisTemplate.expire(key, Duration.ofSeconds(timeWindowSeconds));
        }

        // Check if the request count exceeds the maximum allowed
        return currentCount <= maxRequests;
    }
}

