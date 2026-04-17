package com.peciatech.ibold.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class QuotaExceededException extends ApiException {
    public QuotaExceededException(String message, Map<String, Object> details) {
        super(HttpStatus.PAYMENT_REQUIRED, "QUOTA_EXHAUSTED", message, details);
    }
}
