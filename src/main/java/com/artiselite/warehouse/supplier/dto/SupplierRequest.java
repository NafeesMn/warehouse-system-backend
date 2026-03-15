package com.artiselite.warehouse.supplier.dto;

import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
        @NotBlank String supplierName,
        String contactPerson,
        String phone,
        String email,
        String address
) {
}
