package com.artiselite.warehouse.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 150, message = "fullName must not exceed 150 characters.")
        String fullName,
        @Email(message = "email must be a valid email address.")
        @Size(max = 150, message = "email must not exceed 150 characters.")
        String email,
        @Pattern(regexp = "^(?i)(MANAGER|OPERATOR)$", message = "roleName must be MANAGER or OPERATOR.")
        String roleName
) {
}