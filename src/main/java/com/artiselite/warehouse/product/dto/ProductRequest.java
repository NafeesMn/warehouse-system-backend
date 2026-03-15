package com.artiselite.warehouse.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        String tags,
        BigDecimal unitPrice,
        @Min(value = 0, message = "Current stock must be zero or greater.") Integer currentStock,
        @Min(value = 0, message = "Reorder level must be zero or greater.") Integer reorderLevel
) {
}
