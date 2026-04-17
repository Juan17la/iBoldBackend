package com.peciatech.ibold.api.dto;

import com.peciatech.ibold.domain.Plan;

public record UpgradePlanResponse(
        String email,
        Plan previousPlan,
        Plan currentPlan,
        String message
) {
}
