package com.artiselite.warehouse.inbound.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.exception.BadRequestException;
import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.inbound.dto.request.CreateInboundRequest;
import com.artiselite.warehouse.inbound.dto.response.InboundListItemResponse;
import com.artiselite.warehouse.inbound.dto.response.InboundResponse;
import com.artiselite.warehouse.inbound.entity.InboundTransaction;
import com.artiselite.warehouse.inbound.mapper.InboundMapper;
import com.artiselite.warehouse.inbound.repository.InboundTransactionRepository;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.supplier.entity.Supplier;
import com.artiselite.warehouse.supplier.repository.SupplierRepository;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class InboundServiceImplTest {

    @Mock
    private InboundTransactionRepository inboundTransactionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private UserRepository userRepository;

    private InboundServiceImpl inboundService;

    @BeforeEach
    void setUp() {
        inboundService = new InboundServiceImpl(
                inboundTransactionRepository,
                productRepository,
                supplierRepository,
                userRepository,
                new InboundMapper()
        );
    }

    @Test
    void createInboundShouldSucceed() {
        Product product = buildProduct(101L, 10);
        Supplier supplier = buildSupplier(12L);
        User user = buildUser(5L, "operator@artiselite.local");
        CreateInboundRequest request = new CreateInboundRequest(
                101L,
                12L,
                7,
                LocalDateTime.of(2026, 3, 15, 11, 0),
                "GRN-1001",
                "Morning receipt"
        );

        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(12L)).thenReturn(Optional.of(supplier));
        when(userRepository.findByEmailIgnoreCase("operator@artiselite.local")).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inboundTransactionRepository.save(any(InboundTransaction.class))).thenAnswer(invocation -> {
            InboundTransaction inbound = invocation.getArgument(0);
            inbound.setInboundId(1001L);
            inbound.setCreatedAt(LocalDateTime.of(2026, 3, 15, 11, 5));
            return inbound;
        });

        InboundResponse response = inboundService.createInbound(request, "operator@artiselite.local");

        assertThat(response.inboundId()).isEqualTo(1001L);
        assertThat(response.productId()).isEqualTo(101L);
        assertThat(response.supplierId()).isEqualTo(12L);
        assertThat(response.quantity()).isEqualTo(7);
        assertThat(response.stockAfterUpdate()).isEqualTo(17);
    }

    @Test
    void createInboundShouldRejectInvalidQuantity() {
        CreateInboundRequest request = new CreateInboundRequest(
                101L,
                12L,
                0,
                LocalDateTime.of(2026, 3, 15, 11, 0),
                null,
                null
        );

        assertThrows(BadRequestException.class, () -> inboundService.createInbound(request, "operator@artiselite.local"));
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void createInboundShouldRejectMissingProduct() {
        CreateInboundRequest request = new CreateInboundRequest(
                999L,
                12L,
                5,
                LocalDateTime.of(2026, 3, 15, 11, 0),
                null,
                null
        );

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inboundService.createInbound(request, "operator@artiselite.local"));
    }

    @Test
    void createInboundShouldIncreaseStockCorrectly() {
        Product product = buildProduct(101L, 3);
        Supplier supplier = buildSupplier(12L);
        User user = buildUser(5L, "manager@artiselite.local");
        CreateInboundRequest request = new CreateInboundRequest(
                101L,
                12L,
                9,
                LocalDateTime.of(2026, 3, 15, 13, 0),
                "GRN-1002",
                "Afternoon receipt"
        );

        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(12L)).thenReturn(Optional.of(supplier));
        when(userRepository.findByEmailIgnoreCase("manager@artiselite.local")).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inboundTransactionRepository.save(any(InboundTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        inboundService.createInbound(request, "manager@artiselite.local");

        assertThat(product.getCurrentStock()).isEqualTo(12);
        verify(productRepository).save(product);
    }

    @Test
    void getInboundsShouldReturnPagedHistory() {
        InboundTransaction inbound = buildInbound(2001L, buildProduct(101L, 20), buildSupplier(12L), buildUser(5L, "operator@artiselite.local"));
        when(inboundTransactionRepository.findAllByFilters(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(inbound)));

        PagedResponse<InboundListItemResponse> response = inboundService.getInbounds(null, null, 0, 10, "desc");

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).inboundId()).isEqualTo(2001L);
        assertThat(response.items().get(0).productSku()).isEqualTo("SKU-101");
        verify(inboundTransactionRepository).findAllByFilters(any(), any(), any(Pageable.class));
    }

    private Product buildProduct(Long productId, int currentStock) {
        Product product = new Product();
        product.setProductId(productId);
        product.setSku("SKU-" + productId);
        product.setName("Product " + productId);
        product.setDescription("Warehouse stock item");
        product.setTags("hardware");
        product.setUnitPrice(new BigDecimal("10.00"));
        product.setCurrentStock(currentStock);
        product.setReorderLevel(5);
        product.setCreatedAt(LocalDateTime.of(2026, 3, 15, 8, 0));
        product.setUpdatedAt(LocalDateTime.of(2026, 3, 15, 8, 30));
        return product;
    }

    private Supplier buildSupplier(Long supplierId) {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(supplierId);
        supplier.setSupplierName("Supplier " + supplierId);
        supplier.setCreatedAt(LocalDateTime.of(2026, 3, 15, 8, 0));
        supplier.setUpdatedAt(LocalDateTime.of(2026, 3, 15, 8, 30));
        return supplier;
    }

    private User buildUser(Long userId, String email) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setFullName("Warehouse User");
        return user;
    }

    private InboundTransaction buildInbound(Long inboundId, Product product, Supplier supplier, User user) {
        InboundTransaction inbound = new InboundTransaction();
        inbound.setInboundId(inboundId);
        inbound.setProduct(product);
        inbound.setSupplier(supplier);
        inbound.setQuantity(6);
        inbound.setReceivedDate(LocalDateTime.of(2026, 3, 15, 14, 0));
        inbound.setReferenceNo("GRN-2001");
        inbound.setRemarks("Inbound history entry");
        inbound.setCreatedBy(user);
        inbound.setCreatedAt(LocalDateTime.of(2026, 3, 15, 14, 5));
        inbound.setStockAfterUpdate(26);
        return inbound;
    }
}