package com.peciatech.ibold.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String error;
    private final Map<String, Object> details;

    public ApiException(HttpStatus status, String error, String message) {
        this(status, error, message, Map.of());
    }

    public ApiException(HttpStatus status, String error, String message, Map<String, Object> details) {
        super(message);
        this.status = status;
        this.error = error;
        this.details = details == null ? Map.of() : details;
    }
}
