package com.artiselite.warehouse.auth.dto;

import java.time.Instant;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        Long userId,
        String fullName,
        String email,
        String role
) {
}
