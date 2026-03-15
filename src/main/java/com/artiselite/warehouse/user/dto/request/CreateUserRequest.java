package com.artiselite.warehouse.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "fullName is required.")
        @Size(max = 150, message = "fullName must not exceed 150 characters.")
        String fullName,
        @NotBlank(message = "email is required.")
        @Email(message = "email must be a valid email address.")
        @Size(max = 150, message = "email must not exceed 150 characters.")
        String email,
        @NotBlank(message = "password is required.")
        @Size(min = 8, message = "password must be at least 8 characters.")
        String password,
        @NotBlank(message = "roleName is required.")
        @Pattern(regexp = "^(?i)(MANAGER|OPERATOR)$", message = "roleName must be MANAGER or OPERATOR.")
        String roleName,
        Boolean isActive
) {
}