package com.artiselite.warehouse.product.mapper;

import com.artiselite.warehouse.product.dto.request.CreateProductRequest;
import com.artiselite.warehouse.product.dto.request.UpdateProductRequest;
import com.artiselite.warehouse.product.dto.response.ProductListItemResponse;
import com.artiselite.warehouse.product.dto.response.ProductResponse;
import com.artiselite.warehouse.product.entity.Product;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {
        Product product = new Product();
        apply(
                product,
                request.sku(),
                request.name(),
                request.description(),
                request.tags(),
                request.unitPrice(),
                request.currentStock(),
                request.reorderLevel()
        );
        return product;
    }

    public void updateEntity(Product product, UpdateProductRequest request) {
        apply(
                product,
                request.sku(),
                request.name(),
                request.description(),
                request.tags(),
                request.unitPrice(),
                request.currentStock(),
                request.reorderLevel()
        );
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getTags(),
                product.getUnitPrice(),
                product.getCurrentStock(),
                product.getReorderLevel(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public ProductListItemResponse toListItemResponse(Product product) {
        return new ProductListItemResponse(
                product.getProductId(),
                product.getSku(),
                product.getName(),
                product.getTags(),
                product.getUnitPrice(),
                product.getCurrentStock(),
                product.getReorderLevel(),
                product.getUpdatedAt()
        );
    }

    private void apply(
            Product product,
            String sku,
            String name,
            String description,
            String tags,
            BigDecimal unitPrice,
            Integer currentStock,
            Integer reorderLevel
    ) {
        product.setSku(sku.trim());
        product.setName(name.trim());
        product.setDescription(normalizeNullableText(description));
        product.setTags(normalizeNullableText(tags));
        product.setUnitPrice(unitPrice);
        product.setCurrentStock(currentStock);
        product.setReorderLevel(reorderLevel);
    }

    private String normalizeNullableText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}