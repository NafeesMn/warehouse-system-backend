package com.artiselite.warehouse.inbound.dto.response;

import java.time.LocalDateTime;

public record InboundResponse(
        Long inboundId,
        Long productId,
        String productSku,
        String productName,
        Long supplierId,
        String supplierName,
        Integer quantity,
        LocalDateTime receivedDate,
        String referenceNo,
        String remarks,
        Long createdBy,
        String createdByName,
        LocalDateTime createdAt,
        Integer stockAfterUpdate
) {
}