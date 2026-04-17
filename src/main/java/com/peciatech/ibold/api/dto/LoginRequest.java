package com.peciatech.ibold.api.dto;

public record LoginRequest(
        String email,
        String password
) {
}
