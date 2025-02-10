package com.queue.worker.service;

import com.queue.worker.entity.Queue;
import com.queue.worker.repository.QueueRepository;
import com.queue.worker.runner.Request;
import com.queue.worker.runner.RequestQueue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueWorkerService {

    private final RestTemplate restTemplate;
    private final StringRedisTemplate redisTemplate;
    private final RequestQueue requestQueue;
    private final QueueRepository queueRepository;
    private static final String RATE_LIMIT_TTL_KEY = "try/rate-limit-ttl";
    private static final String RATE_LIMIT_SIZE_KEY = "try/rate-limit-size";

    @Value("${external.for-which-service}")
    private String forWhichService;

    @Value("${external.ws-url}")
    private String wsUrl;

    @SneakyThrows
    public String queueWorker() {

        ResponseEntity<String> response;

        if (acquireRateLimitSlot(forWhichService)) {
            response = restTemplate.getForEntity(wsUrl, String.class);
        } else {

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Istanbul"));
            Timestamp timestamp = Timestamp.valueOf(now);
            Queue queue = queueRepository.save(Queue.builder().whichExternalService(forWhichService).successStatus(Boolean.FALSE).build());

            CompletableFuture<ResponseEntity<String>> responseTryRateLimit = new CompletableFuture<>();
            Request requestQueueData = new Request(responseTryRateLimit, forWhichService, timestamp.getTime(), queue.getId());
            requestQueue.add(requestQueueData);
            log.info(forWhichService + " acquireRateLimitSlot is full. We have to wait for entity id : " + queue.getId());
            response = responseTryRateLimit.get();
        }

        return response.getBody();

    }

    private boolean acquireRateLimitSlot(String method) {
        // Generate a Redis key to track the rate limit for the given method
        String key = method + "-rate-limit";

        // Retrieve the configured time-to-live (TTL) and maximum queue size from Redis
        int ttlOfQueue = Integer.valueOf(redisTemplate.opsForValue().get(RATE_LIMIT_TTL_KEY));
        int sizeOfQueue = Integer.valueOf(redisTemplate.opsForValue().get(RATE_LIMIT_SIZE_KEY));

        // Get the current count of requests from Redis
        String currentCountStr = redisTemplate.opsForValue().get(key);
        Long currentCount = (currentCountStr != null) ? Long.parseLong(currentCountStr) : 0L;

        // If the current count has reached the maximum allowed size, return false (no slot available)
        if (currentCount >= sizeOfQueue)
            return false;

        // Increment the current count atomically in Redis
        Long currentCountLast = redisTemplate.opsForValue().increment(key);

        // If this is the first request in the current time window, set the expiration time (TTL) on the key
        if (currentCountLast == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(ttlOfQueue));
        }

        // Return true if the incremented count is within the allowed limit, false otherwise
        return currentCountLast <= sizeOfQueue;
    }

}
