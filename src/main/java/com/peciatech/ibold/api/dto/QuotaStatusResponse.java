package com.peciatech.ibold.api.dto;

import com.peciatech.ibold.domain.Plan;
import java.time.LocalDate;

public record QuotaStatusResponse(
        String email,
        Plan currentPlan,
        long usedTokens,
        Long remainingTokens,
        LocalDate resetDate
) {
}
