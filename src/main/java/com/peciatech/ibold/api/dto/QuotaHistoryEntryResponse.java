package com.peciatech.ibold.api.dto;

import java.time.LocalDate;

public record QuotaHistoryEntryResponse(
        LocalDate date,
        long usedTokens
) {
}
