package com.peciatech.ibold.api.dto;

import com.peciatech.ibold.domain.Plan;

public record RegisterUserRequest(
        String email,
        String password,
        String name,
        Plan plan
) {
}
