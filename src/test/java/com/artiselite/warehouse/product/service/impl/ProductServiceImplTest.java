package com.artiselite.warehouse.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.exception.DuplicateResourceException;
import com.artiselite.warehouse.product.dto.request.CreateProductRequest;
import com.artiselite.warehouse.product.dto.request.UpdateProductRequest;
import com.artiselite.warehouse.product.dto.response.ProductListItemResponse;
import com.artiselite.warehouse.product.dto.response.ProductResponse;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.mapper.ProductMapper;
import com.artiselite.warehouse.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, new ProductMapper());
    }

    @Test
    void createProductShouldSucceed() {
        CreateProductRequest request = new CreateProductRequest(
                "SKU-001",
                "Widget A",
                "Main shelf widget",
                "hardware,blue",
                new BigDecimal("12.50"),
                25,
                5
        );

        when(productRepository.existsBySkuIgnoreCase("SKU-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setProductId(1L);
            product.setCreatedAt(LocalDateTime.of(2026, 3, 15, 9, 0));
            product.setUpdatedAt(LocalDateTime.of(2026, 3, 15, 9, 0));
            return product;
        });

        ProductResponse response = productService.createProduct(request);

        assertThat(response.productId()).isEqualTo(1L);
        assertThat(response.sku()).isEqualTo("SKU-001");
        assertThat(response.name()).isEqualTo("Widget A");
        assertThat(response.currentStock()).isEqualTo(25);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProductShouldRejectDuplicateSku() {
        CreateProductRequest request = new CreateProductRequest(
                "SKU-001",
                "Widget A",
                null,
                null,
                null,
                10,
                2
        );

        when(productRepository.existsBySkuIgnoreCase("SKU-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(request));
    }

    @Test
    void updateProductShouldSucceed() {
        Product existingProduct = buildProduct(1L, "SKU-001", "Widget A");
        UpdateProductRequest request = new UpdateProductRequest(
                "SKU-002",
                "Widget B",
                "Updated description",
                "hardware,green",
                new BigDecimal("20.00"),
                40,
                10
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySkuIgnoreCaseAndProductIdNot("SKU-002", 1L)).thenReturn(false);
        when(productRepository.save(existingProduct)).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setUpdatedAt(LocalDateTime.of(2026, 3, 15, 10, 30));
            return product;
        });

        ProductResponse response = productService.updateProduct(1L, request);

        assertThat(response.sku()).isEqualTo("SKU-002");
        assertThat(response.name()).isEqualTo("Widget B");
        assertThat(response.tags()).isEqualTo("hardware,green");
        assertThat(response.currentStock()).isEqualTo(40);
    }

    @Test
    void getProductByIdShouldReturnProduct() {
        Product product = buildProduct(5L, "SKU-005", "Widget Detail");
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(5L);

        assertThat(response.productId()).isEqualTo(5L);
        assertThat(response.name()).isEqualTo("Widget Detail");
    }

    @Test
    void searchProductsShouldReturnMatchesForKeyword() {
        Product product = buildProduct(7L, "BOLT-001", "Steel Bolt");
        PageRequest pageRequest = PageRequest.of(0, 10, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "name"));
        when(productRepository.searchByKeyword("bolt", pageRequest))
                .thenReturn(new PageImpl<>(java.util.List.of(product), pageRequest, 1));

        PagedResponse<ProductListItemResponse> response =
                productService.searchProducts("bolt", null, 0, 10, "name", "asc");

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).sku()).isEqualTo("BOLT-001");
        assertThat(response.totalElements()).isEqualTo(1);
    }

    private Product buildProduct(Long id, String sku, String name) {
        Product product = new Product();
        product.setProductId(id);
        product.setSku(sku);
        product.setName(name);
        product.setDescription("Warehouse product");
        product.setTags("hardware,fastener");
        product.setUnitPrice(new BigDecimal("5.50"));
        product.setCurrentStock(15);
        product.setReorderLevel(5);
        product.setCreatedAt(LocalDateTime.of(2026, 3, 15, 8, 0));
        product.setUpdatedAt(LocalDateTime.of(2026, 3, 15, 8, 30));
        return product;
    }
}