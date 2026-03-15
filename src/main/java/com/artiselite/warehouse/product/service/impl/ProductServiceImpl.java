package com.artiselite.warehouse.product.service.impl;

import com.artiselite.warehouse.exception.DuplicateResourceException;
import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.product.dto.ProductRequest;
import com.artiselite.warehouse.product.dto.ProductResponse;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.product.service.ProductService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return toResponse(getRequiredProduct(productId));
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new DuplicateResourceException("SKU already exists.");
        }

        Product product = new Product();
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = getRequiredProduct(productId);
        if (!product.getSku().equalsIgnoreCase(request.sku())
                && productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new DuplicateResourceException("SKU already exists.");
        }

        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    private Product getRequiredProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setSku(request.sku().trim());
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setTags(request.tags());
        product.setUnitPrice(request.unitPrice());
        product.setCurrentStock(request.currentStock() == null ? 0 : request.currentStock());
        product.setReorderLevel(request.reorderLevel() == null ? 0 : request.reorderLevel());
    }

    private ProductResponse toResponse(Product product) {
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
}
