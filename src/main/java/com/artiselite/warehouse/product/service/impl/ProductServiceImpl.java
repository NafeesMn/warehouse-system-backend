package com.artiselite.warehouse.product.service.impl;

import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.exception.BadRequestException;
import com.artiselite.warehouse.exception.DuplicateResourceException;
import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.product.dto.request.CreateProductRequest;
import com.artiselite.warehouse.product.dto.request.UpdateProductRequest;
import com.artiselite.warehouse.product.dto.response.ProductListItemResponse;
import com.artiselite.warehouse.product.dto.response.ProductResponse;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.mapper.ProductMapper;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.product.service.ProductService;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "productId",
            "sku",
            "name",
            "unitPrice",
            "currentStock",
            "reorderLevel",
            "createdAt",
            "updatedAt"
    );

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        String normalizedSku = normalizeRequiredText(request.sku(), "SKU is required.");
        validateProductFields(request.name(), request.currentStock(), request.reorderLevel(), request.unitPrice());

        if (productRepository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new DuplicateResourceException("SKU already exists: " + normalizedSku);
        }

        Product product = productMapper.toEntity(request);
        product.setSku(normalizedSku);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public PagedResponse<ProductListItemResponse> getProducts(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDirection);
        Page<ProductListItemResponse> result = productRepository.findAll(pageable)
                .map(productMapper::toListItemResponse);
        return PagedResponse.from(result);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return productMapper.toResponse(getRequiredProduct(productId));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = getRequiredProduct(productId);
        String normalizedSku = normalizeRequiredText(request.sku(), "SKU is required.");
        validateProductFields(request.name(), request.currentStock(), request.reorderLevel(), request.unitPrice());

        if (productRepository.existsBySkuIgnoreCaseAndProductIdNot(normalizedSku, productId)) {
            throw new DuplicateResourceException("SKU already exists: " + normalizedSku);
        }

        productMapper.updateEntity(product, request);
        product.setSku(normalizedSku);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public PagedResponse<ProductListItemResponse> searchProducts(
            String keyword,
            String tag,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        String normalizedKeyword = normalizeOptionalText(keyword);
        String normalizedTag = normalizeOptionalText(tag);
        if (!StringUtils.hasText(normalizedKeyword) && !StringUtils.hasText(normalizedTag)) {
            throw new BadRequestException("At least one search filter is required: keyword or tag.");
        }

        Pageable pageable = buildPageable(page, size, sortBy, sortDirection);
        Page<Product> result;
        if (StringUtils.hasText(normalizedKeyword) && StringUtils.hasText(normalizedTag)) {
            result = productRepository.searchByKeywordAndTag(normalizedKeyword, normalizedTag, pageable);
        } else if (StringUtils.hasText(normalizedKeyword)) {
            result = productRepository.searchByKeyword(normalizedKeyword, pageable);
        } else {
            result = productRepository.findByTagsContainingIgnoreCase(normalizedTag, pageable);
        }

        return PagedResponse.from(result.map(productMapper::toListItemResponse));
    }

    private Product getRequiredProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDirection) {
        if (page < 0) {
            throw new BadRequestException("Page must be zero or greater.");
        }
        if (size <= 0 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("Size must be between 1 and 100.");
        }

        String normalizedSortBy = StringUtils.hasText(sortBy) ? sortBy.trim() : "name";
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortBy)) {
            throw new BadRequestException("Unsupported sort field: " + normalizedSortBy);
        }

        Sort.Direction direction;
        try {
            direction = StringUtils.hasText(sortDirection)
                    ? Sort.Direction.fromString(sortDirection.trim())
                    : Sort.Direction.ASC;
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Sort direction must be ASC or DESC.");
        }

        return PageRequest.of(page, size, Sort.by(direction, normalizedSortBy));
    }

    private void validateProductFields(
            String name,
            Integer currentStock,
            Integer reorderLevel,
            BigDecimal unitPrice
    ) {
        normalizeRequiredText(name, "Product name is required.");
        if (currentStock == null || currentStock < 0) {
            throw new BadRequestException("Current stock must not be negative.");
        }
        if (reorderLevel == null || reorderLevel < 0) {
            throw new BadRequestException("Reorder level must not be negative.");
        }
        if (unitPrice != null && unitPrice.signum() < 0) {
            throw new BadRequestException("Unit price must not be negative.");
        }
    }

    private String normalizeRequiredText(String value, String message) {
        String normalized = normalizeOptionalText(value);
        if (!StringUtils.hasText(normalized)) {
            throw new BadRequestException(message);
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}