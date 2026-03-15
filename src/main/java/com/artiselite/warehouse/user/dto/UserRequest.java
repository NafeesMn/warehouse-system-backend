package com.artiselite.warehouse.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @Size(min = 8, message = "Password must be at least 8 characters when provided.") String password,
        @NotBlank String roleName,
        Boolean isActive
) {
}
