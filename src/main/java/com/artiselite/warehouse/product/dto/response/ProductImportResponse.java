package com.artiselite.warehouse.product.dto.response;

import java.util.List;

public record ProductImportResponse(
        int insertedCount,
        int updatedCount,
        int failedCount,
        List<ProductImportRowErrorResponse> errors
) {
}