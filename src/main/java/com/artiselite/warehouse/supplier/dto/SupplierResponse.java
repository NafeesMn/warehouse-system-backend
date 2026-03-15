package com.artiselite.warehouse.supplier.dto;

import java.time.LocalDateTime;

public record SupplierResponse(
        Long supplierId,
        String supplierName,
        String contactPerson,
        String phone,
        String email,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
