package com.peciatech.ibold.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitState {
    private String userId;
    private int requestCount;
    private Instant windowStart;
}
