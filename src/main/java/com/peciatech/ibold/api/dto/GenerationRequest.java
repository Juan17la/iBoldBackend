package com.peciatech.ibold.api.dto;

public record GenerationRequest(
        String email,
        String password,
        String prompt,
        Integer requestedTokens
) {
}
