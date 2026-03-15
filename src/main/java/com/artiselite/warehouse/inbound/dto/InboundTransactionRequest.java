package com.artiselite.warehouse.inbound.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record InboundTransactionRequest(
        @NotNull Long productId,
        @NotNull Long supplierId,
        @NotNull @Min(value = 1, message = "Quantity must be greater than zero.") Integer quantity,
        @NotNull LocalDateTime receivedDate,
        String referenceNo,
        String remarks
) {
}
