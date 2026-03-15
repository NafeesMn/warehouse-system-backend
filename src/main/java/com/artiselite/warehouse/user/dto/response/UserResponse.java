package com.artiselite.warehouse.user.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String fullName,
        String email,
        String roleName,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}