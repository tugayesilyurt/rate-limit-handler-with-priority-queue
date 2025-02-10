package com.queue.worker.runner;

import com.queue.worker.entity.Queue;
import com.queue.worker.repository.QueueRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class QueueWorker {

    private final RequestQueue requestQueue;
    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate;
    private final QueueRepository queueRepository;
    private static final String RATE_LIMIT_TTL_KEY = "try/rate-limit-ttl";
    private static final String RATE_LIMIT_SIZE_KEY = "try/rate-limit-size";

    @Value("${external.for-which-service}")
    private String forWhichService;

    @Value("${external.ws-url}")
    private String wsUrl;

    @Scheduled(fixedRate = 1000)
    public void processQueue() {
        while (!requestQueue.isEmpty()) {
            Request request = requestQueue.poll();
            if (request == null) {
                return;
            }
            try {
                if (acquireRateLimitSlot(request.getMethod())) {

                    ResponseEntity<String> response = restTemplate.getForEntity(wsUrl, String.class);
                    Optional<Queue> queue = queueRepository.findById(request.getEntityId());
                    if(queue.isPresent()){
                        queue.get().setSuccessStatus(Boolean.TRUE);
                        queue.get().setUpdatedDate(LocalDateTime.now());
                        queueRepository.save(queue.get());
                    }
                    log.info("QueueWorker worked " +  response.getBody());
                    request.getResponse().complete(response);
                } else {
                    log.info("acquireRateLimitSlot full : " + request.getEntityId());
                    requestQueue.add(request);
                }
            } catch (Exception e) {
               log.error("QueueWorker processQueue error " + e.getMessage());
            }
        }
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
