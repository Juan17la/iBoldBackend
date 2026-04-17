package com.peciatech.ibold.api.dto;

import com.peciatech.ibold.domain.Plan;

public record LoginResponse(
        String email,
        String name,
        Plan plan,
        String message
) {
}
