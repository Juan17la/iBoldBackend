package com.peciatech.ibold.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RateLimitExceededException extends ApiException {
    private final long retryAfterSeconds;

    public RateLimitExceededException(String message, long retryAfterSeconds, Map<String, Object> details) {
        super(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", message, details);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
