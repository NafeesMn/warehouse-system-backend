package com.artiselite.warehouse.outbound.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record OutboundTransactionRequest(
        @NotNull Long productId,
        @NotNull Long customerId,
        @NotNull @Min(value = 1, message = "Quantity must be greater than zero.") Integer quantity,
        @NotNull LocalDateTime shippedDate,
        String referenceNo,
        String remarks
) {
}
