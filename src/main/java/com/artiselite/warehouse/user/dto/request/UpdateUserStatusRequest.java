package com.artiselite.warehouse.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "isActive is required.")
        Boolean isActive
) {
}