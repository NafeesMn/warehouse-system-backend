package com.artiselite.warehouse.customer.dto;

import java.time.LocalDateTime;

public record CustomerResponse(
        Long customerId,
        String customerName,
        String contactPerson,
        String phone,
        String email,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
