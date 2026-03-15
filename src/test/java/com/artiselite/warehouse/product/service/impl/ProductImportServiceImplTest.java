package com.artiselite.warehouse.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.artiselite.warehouse.product.dto.response.ProductImportResponse;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ProductImportServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductImportServiceImpl productImportService;

    @BeforeEach
    void setUp() {
        productImportService = new ProductImportServiceImpl(productRepository);
    }

    @Test
    void importProductsShouldInsertAndUpdateRows() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                "text/csv",
                ("sku,name,description,tags,unitPrice,currentStock,reorderLevel\n"
                        + "SKU-2001,Marble Tray,Decor tray,decor,55.00,18,6\n"
                        + "SKU-1002,Oak Side Table,Updated table,furniture,159.00,15,5\n").getBytes()
        );
        Product existingProduct = buildProduct("SKU-1002", "Old Oak Side Table", 10);

        when(productRepository.findBySkuIgnoreCase("SKU-2001")).thenReturn(Optional.empty());
        when(productRepository.findBySkuIgnoreCase("SKU-1002")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductImportResponse response = productImportService.importProducts(file);

        assertThat(response.insertedCount()).isEqualTo(1);
        assertThat(response.updatedCount()).isEqualTo(1);
        assertThat(response.failedCount()).isEqualTo(0);
        assertThat(existingProduct.getUnitPrice()).isEqualByComparingTo(new BigDecimal("159.00"));
        assertThat(existingProduct.getCurrentStock()).isEqualTo(15);
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void importProductsShouldCaptureInvalidRows() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                "text/csv",
                ("sku,name,description,tags,unitPrice,currentStock,reorderLevel\n"
                        + "SKU-3001,Valid Lamp,Decor lamp,lighting,70.00,9,3\n"
                        + "SKU-3002,Bad Stock,Problem row,lighting,70.00,-2,3\n").getBytes()
        );

        when(productRepository.findBySkuIgnoreCase("SKU-3001")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductImportResponse response = productImportService.importProducts(file);

        assertThat(response.insertedCount()).isEqualTo(1);
        assertThat(response.updatedCount()).isEqualTo(0);
        assertThat(response.failedCount()).isEqualTo(1);
        assertThat(response.errors()).hasSize(1);
        assertThat(response.errors().get(0).sku()).isEqualTo("SKU-3002");
    }

    private Product buildProduct(String sku, String name, int currentStock) {
        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setCurrentStock(currentStock);
        product.setReorderLevel(5);
        product.setUnitPrice(new BigDecimal("120.00"));
        return product;
    }
}