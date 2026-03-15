package com.artiselite.warehouse.inbound.dto;

import java.time.LocalDateTime;

public record InboundTransactionResponse(
        Long inboundId,
        Long productId,
        String productName,
        Long supplierId,
        String supplierName,
        Integer quantity,
        LocalDateTime receivedDate,
        String referenceNo,
        String remarks,
        Long createdById,
        String createdByName,
        LocalDateTime createdAt
) {
}
