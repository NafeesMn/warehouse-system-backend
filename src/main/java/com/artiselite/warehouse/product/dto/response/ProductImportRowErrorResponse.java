package com.artiselite.warehouse.product.dto.response;

public record ProductImportRowErrorResponse(
        int rowNumber,
        String sku,
        String error
) {
}