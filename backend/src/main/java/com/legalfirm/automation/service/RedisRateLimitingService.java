package com.legalfirm.automation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Enhanced Rate Limiting with Redis (for production use)
 * Add this dependency to pom.xml: spring-boot-starter-data-redis
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisRateLimitingService {

    // Uncomment and use this in production with Redis
    /*
    private final RedisTemplate<String, String> redisTemplate;

    public boolean isAllowed(String key, int limit, Duration window) {
        try {
            String redisKey = "rate_limit:" + key;
            Long currentCount = redisTemplate.opsForValue().increment(redisKey);
            
            if (currentCount == 1) {
                redisTemplate.expire(redisKey, window);
            }
            
            return currentCount <= limit;
        } catch (Exception e) {
            log.error("Redis rate limiting failed, allowing request", e);
            return true; // Fail open
        }
    }
    */
}