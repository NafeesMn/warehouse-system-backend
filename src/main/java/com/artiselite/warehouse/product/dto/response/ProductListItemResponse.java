package com.artiselite.warehouse.product.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductListItemResponse(
        Long productId,
        String sku,
        String name,
        String tags,
        BigDecimal unitPrice,
        Integer currentStock,
        Integer reorderLevel,
        LocalDateTime updatedAt
) {
}