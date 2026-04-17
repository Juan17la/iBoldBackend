package com.peciatech.ibold.api.dto;

import com.peciatech.ibold.domain.Plan;
import java.time.Instant;

public record RegisterUserResponse(
        String email,
        String name,
        Plan plan,
        Instant createdAt,
        String message
) {
}
