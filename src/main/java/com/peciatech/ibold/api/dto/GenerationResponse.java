package com.peciatech.ibold.api.dto;

import java.time.Instant;

public record GenerationResponse(
        String email,
        String generatedText,
        int tokensConsumed,
        Instant generatedAt,
        String model
) {
}
