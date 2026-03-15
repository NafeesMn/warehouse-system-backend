package com.artiselite.warehouse.outbound.dto;

import java.time.LocalDateTime;

public record OutboundTransactionResponse(
        Long outboundId,
        Long productId,
        String productName,
        Long customerId,
        String customerName,
        Integer quantity,
        LocalDateTime shippedDate,
        String referenceNo,
        String remarks,
        Long createdById,
        String createdByName,
        LocalDateTime createdAt
) {
}
