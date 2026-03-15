package com.artiselite.warehouse.common.dto;

public record ProductReferenceOptionResponse(
        Long productId,
        String sku,
        String name,
        Integer currentStock
) {
}