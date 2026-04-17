package com.peciatech.ibold.api.dto;

import com.peciatech.ibold.domain.Plan;

public record UpgradePlanRequest(
        String email,
        String password,
        Plan targetPlan
) {
}
