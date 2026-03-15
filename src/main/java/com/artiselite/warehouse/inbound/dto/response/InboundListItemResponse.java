package com.artiselite.warehouse.inbound.dto.response;

import java.time.LocalDateTime;

public record InboundListItemResponse(
        Long inboundId,
        Long productId,
        String productSku,
        String productName,
        Long supplierId,
        String supplierName,
        Integer quantity,
        LocalDateTime receivedDate,
        String referenceNo,
        Long createdBy,
        String createdByName,
        LocalDateTime createdAt,
        Integer stockAfterUpdate
) {
}