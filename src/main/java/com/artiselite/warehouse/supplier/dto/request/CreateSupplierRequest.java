package com.artiselite.warehouse.supplier.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSupplierRequest(
        @NotBlank(message = "Supplier name is required.")
        @Size(max = 150, message = "Supplier name must not exceed 150 characters.")
        String supplierName,

        @Size(max = 150, message = "Contact person must not exceed 150 characters.")
        String contactPerson,

        @Size(max = 50, message = "Phone must not exceed 50 characters.")
        String phone,

        @Email(message = "Email must be valid.")
        @Size(max = 150, message = "Email must not exceed 150 characters.")
        String email,

        @Size(max = 2000, message = "Address must not exceed 2000 characters.")
        String address
) {
}