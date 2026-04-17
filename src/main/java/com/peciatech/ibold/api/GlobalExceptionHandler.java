package com.peciatech.ibold.api;

import com.peciatech.ibold.exception.ApiErrorResponse;
import com.peciatech.ibold.exception.ApiException;
import com.peciatech.ibold.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                ex.getStatus().value(),
                ex.getError(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getDetails()
        );

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(ex.getStatus());
        if (ex instanceof RateLimitExceededException rateLimitException) {
            builder.header(HttpHeaders.RETRY_AFTER, String.valueOf(rateLimitException.getRetryAfterSeconds()));
        }

        return builder.body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handlePayloadError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                "Invalid request payload",
                request.getRequestURI(),
                Map.of("reason", ex.getMostSpecificCause().getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "Unexpected error while processing request",
                request.getRequestURI(),
                Map.of("reason", ex.getMessage() == null ? "Unknown" : ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
