package com.peciatech.ibold.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Plan {
    FREE(10, 50_000L),
    PRO(60, 500_000L),
    ENTERPRISE(-1, -1L);

    private final int requestsPerMinute;
    private final long monthlyTokens;

    public boolean hasUnlimitedRequests() {
        return requestsPerMinute < 0;
    }

    public boolean hasUnlimitedTokens() {
        return monthlyTokens < 0;
    }
}
