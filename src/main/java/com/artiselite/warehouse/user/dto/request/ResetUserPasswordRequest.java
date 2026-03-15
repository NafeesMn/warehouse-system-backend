package com.artiselite.warehouse.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetUserPasswordRequest(
        @NotBlank(message = "newPassword is required.")
        @Size(min = 8, message = "newPassword must be at least 8 characters.")
        String newPassword
) {
}