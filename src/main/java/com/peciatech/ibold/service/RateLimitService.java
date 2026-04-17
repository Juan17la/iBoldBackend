package com.peciatech.ibold.service;

import com.peciatech.ibold.domain.Plan;
import com.peciatech.ibold.domain.model.RateLimitState;
import com.peciatech.ibold.exception.RateLimitExceededException;
import com.peciatech.ibold.repository.RateLimitRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {
    private final RateLimitRepository rateLimitRepository;
    private final UserService userService;

    public RateLimitService(RateLimitRepository rateLimitRepository, UserService userService) {
        this.rateLimitRepository = rateLimitRepository;
        this.userService = userService;
    }

    public void assertWithinLimit(String email) {
        String normalizedEmail = normalizeEmail(email);
        String internalUserId = userService.findByEmailOrThrow(normalizedEmail).getUserId();
        Plan plan = userService.findByUserIdOrThrow(internalUserId).getPlan();
        if (plan.hasUnlimitedRequests()) {
            return;
        }

        Instant now = Instant.now();
        RateLimitState state = rateLimitRepository.findByUserId(internalUserId)
                .orElse(new RateLimitState(internalUserId, 0, now));

        if (isWindowExpired(state.getWindowStart(), now)) {
            state.setWindowStart(now);
            state.setRequestCount(0);
        }

        if (state.getRequestCount() >= plan.getRequestsPerMinute()) {
            long retryAfterSeconds = calculateRetryAfterSeconds();
            throw new RateLimitExceededException(
                    "Rate limit exceeded for plan " + plan.name(),
                    retryAfterSeconds,
                    Map.of(
                            "limitPerMinute", plan.getRequestsPerMinute(),
                            "retryAfterSeconds", retryAfterSeconds
                    )
            );
        }

        state.setRequestCount(state.getRequestCount() + 1);
        rateLimitRepository.save(state);
    }

    public void resetAllLimits() {
        rateLimitRepository.clear();
    }

    private boolean isWindowExpired(Instant windowStart, Instant now) {
        if (windowStart == null) {
            return true;
        }
        return Duration.between(windowStart, now).toSeconds() >= 60;
    }

    private long calculateRetryAfterSeconds() {
        int currentSecond = LocalDateTime.now().getSecond();
        return Math.max(1, 60 - currentSecond);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        return email.trim().toLowerCase();
    }
}
