package com.artiselite.warehouse.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank(message = "SKU is required.")
        @Size(max = 100, message = "SKU must not exceed 100 characters.")
        String sku,

        @NotBlank(message = "Product name is required.")
        @Size(max = 150, message = "Product name must not exceed 150 characters.")
        String name,

        @Size(max = 2000, message = "Description must not exceed 2000 characters.")
        String description,

        @Size(max = 255, message = "Tags must not exceed 255 characters.")
        String tags,

        @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must not be negative.")
        BigDecimal unitPrice,

        @NotNull(message = "Current stock is required.")
        @PositiveOrZero(message = "Current stock must not be negative.")
        Integer currentStock,

        @NotNull(message = "Reorder level is required.")
        @PositiveOrZero(message = "Reorder level must not be negative.")
        Integer reorderLevel
) {
}