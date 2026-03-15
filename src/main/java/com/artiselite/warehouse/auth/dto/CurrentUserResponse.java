package com.artiselite.warehouse.auth.dto;

public record CurrentUserResponse(
        Long userId,
        String fullName,
        String email,
        String role,
        boolean active
) {
}
