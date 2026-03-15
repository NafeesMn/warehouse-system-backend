package com.artiselite.warehouse.outbound.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.artiselite.warehouse.customer.entity.Customer;
import com.artiselite.warehouse.customer.repository.CustomerRepository;
import com.artiselite.warehouse.exception.BadRequestException;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionRequest;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionResponse;
import com.artiselite.warehouse.outbound.entity.OutboundTransaction;
import com.artiselite.warehouse.outbound.repository.OutboundTransactionRepository;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboundTransactionServiceImplTest {

    @Mock
    private OutboundTransactionRepository outboundTransactionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    private OutboundTransactionServiceImpl outboundTransactionService;

    @BeforeEach
    void setUp() {
        outboundTransactionService = new OutboundTransactionServiceImpl(
                outboundTransactionRepository,
                productRepository,
                customerRepository,
                userRepository
        );
    }

    @Test
    void createOutboundShouldDecreaseStock() {
        Product product = buildProduct(100L, "SKU-100", 10);
        Customer customer = buildCustomer(5L);
        User user = buildUser(7L, "operator@artiselite.local");
        OutboundTransactionRequest request = new OutboundTransactionRequest(
                100L,
                5L,
                4,
                LocalDateTime.of(2026, 3, 15, 16, 0),
                "SO-1001",
                "Dispatch order"
        );

        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(customerRepository.findById(5L)).thenReturn(Optional.of(customer));
        when(userRepository.findByEmailIgnoreCase("operator@artiselite.local")).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outboundTransactionRepository.save(any(OutboundTransaction.class))).thenAnswer(invocation -> {
            OutboundTransaction outbound = invocation.getArgument(0);
            outbound.setOutboundId(501L);
            outbound.setCreatedAt(LocalDateTime.of(2026, 3, 15, 16, 5));
            return outbound;
        });

        OutboundTransactionResponse response = outboundTransactionService.createOutboundTransaction(request, "operator@artiselite.local");

        assertThat(response.outboundId()).isEqualTo(501L);
        assertThat(product.getCurrentStock()).isEqualTo(6);
        verify(productRepository).save(product);
    }

    @Test
    void createOutboundShouldRejectInsufficientStock() {
        Product product = buildProduct(100L, "SKU-100", 3);
        OutboundTransactionRequest request = new OutboundTransactionRequest(
                100L,
                5L,
                4,
                LocalDateTime.of(2026, 3, 15, 16, 0),
                "SO-1002",
                null
        );

        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(customerRepository.findById(5L)).thenReturn(Optional.of(buildCustomer(5L)));
        when(userRepository.findByEmailIgnoreCase("operator@artiselite.local"))
                .thenReturn(Optional.of(buildUser(7L, "operator@artiselite.local")));

        assertThrows(
                BadRequestException.class,
                () -> outboundTransactionService.createOutboundTransaction(request, "operator@artiselite.local")
        );
        verify(outboundTransactionRepository, never()).save(any(OutboundTransaction.class));
    }

    private Product buildProduct(Long productId, String sku, int currentStock) {
        Product product = new Product();
        product.setProductId(productId);
        product.setSku(sku);
        product.setName("Product " + productId);
        product.setCurrentStock(currentStock);
        return product;
    }

    private Customer buildCustomer(Long customerId) {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setCustomerName("Customer " + customerId);
        return customer;
    }

    private User buildUser(Long userId, String email) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setFullName("Warehouse User");
        return user;
    }
}