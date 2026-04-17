package com.peciatech.ibold.api.dto;

import java.util.List;

public record QuotaHistoryResponse(
        String email,
        List<QuotaHistoryEntryResponse> dailyUsage
) {
}
