package com.legalfirm.automation.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiting Configuration using in-memory storage
 * For production, consider using Redis for distributed rate limiting
 */
@Configuration
@Slf4j
public class RateLimitingConfig {

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registrationBean;
    }

    @Component
    @Slf4j
    public static class RateLimitingFilter implements Filter {

        // In-memory storage for rate limiting (use Redis in production)
        private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
        
        // Rate limit configurations
        private static final int AUTH_REQUESTS_PER_MINUTE = 5;
        private static final int GENERAL_REQUESTS_PER_MINUTE = 60;
        private static final int FILE_UPLOAD_REQUESTS_PER_MINUTE = 10;
        private static final Duration WINDOW_DURATION = Duration.ofMinutes(1);

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String clientId = getClientIdentifier(httpRequest);
            String endpoint = httpRequest.getRequestURI();
            
            // Apply different limits based on endpoint
            int limit = determineRateLimit(endpoint);
            
            if (!isRequestAllowed(clientId, endpoint, limit)) {
                log.warn("Rate limit exceeded for client: {} on endpoint: {}", clientId, endpoint);
                sendRateLimitResponse(httpResponse, limit);
                return;
            }

            // Clean up old entries periodically
            cleanupExpiredEntries();
            
            chain.doFilter(request, response);
        }

        private String getClientIdentifier(HttpServletRequest request) {
            // Try to get user ID from JWT token first
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Extract user ID from JWT token (simplified)
                // In real implementation, decode JWT and get user ID
                return "user_" + authHeader.hashCode();
            }
            
            // Fall back to IP address
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            
            return request.getRemoteAddr();
        }

        private int determineRateLimit(String endpoint) {
            if (endpoint.contains("/api/auth/")) {
                return AUTH_REQUESTS_PER_MINUTE;
            } else if (endpoint.contains("/api/documents/upload")) {
                return FILE_UPLOAD_REQUESTS_PER_MINUTE;
            } else {
                return GENERAL_REQUESTS_PER_MINUTE;
            }
        }

        private boolean isRequestAllowed(String clientId, String endpoint, int limit) {
            String key = clientId + ":" + getEndpointCategory(endpoint);
            RequestCounter counter = requestCounts.computeIfAbsent(key, k -> new RequestCounter());
            
            synchronized (counter) {
                long now = System.currentTimeMillis();
                
                // Reset counter if window has expired
                if (now - counter.getWindowStart() > WINDOW_DURATION.toMillis()) {
                    counter.reset(now);
                }
                
                // Check if limit exceeded
                if (counter.getCount() >= limit) {
                    return false;
                }
                
                // Increment counter
                counter.increment();
                return true;
            }
        }

        private String getEndpointCategory(String endpoint) {
            if (endpoint.contains("/api/auth/")) return "auth";
            if (endpoint.contains("/api/documents/upload")) return "upload";
            return "general";
        }

        private void sendRateLimitResponse(HttpServletResponse response, int limit) throws IOException {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(String.format("""
                {
                    "timestamp": "%s",
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "Rate limit exceeded. Maximum %d requests per minute allowed.",
                    "errorCode": "RATE_LIMIT_EXCEEDED"
                }
                """, java.time.LocalDateTime.now(), limit));
        }

        private void cleanupExpiredEntries() {
            // Cleanup every 100 requests (simple approach)
            if (requestCounts.size() % 100 == 0) {
                long now = System.currentTimeMillis();
                requestCounts.entrySet().removeIf(entry -> 
                    now - entry.getValue().getWindowStart() > WINDOW_DURATION.toMillis() * 2
                );
            }
        }

        /**
         * Request counter for tracking requests within a time window
         */
        private static class RequestCounter {
            private final AtomicInteger count = new AtomicInteger(0);
            private volatile long windowStart = System.currentTimeMillis();

            public void increment() {
                count.incrementAndGet();
            }

            public int getCount() {
                return count.get();
            }

            public long getWindowStart() {
                return windowStart;
            }

            public void reset(long newWindowStart) {
                count.set(0);
                windowStart = newWindowStart;
            }
        }
    }
}