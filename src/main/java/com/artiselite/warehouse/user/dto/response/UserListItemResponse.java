package com.artiselite.warehouse.user.dto.response;

import java.time.LocalDateTime;

public record UserListItemResponse(
        Long userId,
        String fullName,
        String email,
        String roleName,
        boolean isActive,
        LocalDateTime updatedAt
) {
}