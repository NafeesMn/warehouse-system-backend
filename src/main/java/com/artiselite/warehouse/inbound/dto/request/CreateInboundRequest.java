package com.artiselite.warehouse.inbound.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateInboundRequest(
        @NotNull(message = "Product ID is required.")
        Long productId,

        @NotNull(message = "Supplier ID is required.")
        Long supplierId,

        @NotNull(message = "Quantity is required.")
        @Min(value = 1, message = "Quantity must be greater than zero.")
        Integer quantity,

        @NotNull(message = "Received date is required.")
        LocalDateTime receivedDate,

        @Size(max = 100, message = "Reference number must not exceed 100 characters.")
        String referenceNo,

        @Size(max = 2000, message = "Remarks must not exceed 2000 characters.")
        String remarks
) {
}