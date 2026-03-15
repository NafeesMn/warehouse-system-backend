package com.artiselite.warehouse.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String fullName,
        String email,
        boolean active,
        Long roleId,
        String roleName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
