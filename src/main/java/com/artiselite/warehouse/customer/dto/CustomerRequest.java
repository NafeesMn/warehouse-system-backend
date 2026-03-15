package com.artiselite.warehouse.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank String customerName,
        String contactPerson,
        String phone,
        String email,
        String address
) {
}
