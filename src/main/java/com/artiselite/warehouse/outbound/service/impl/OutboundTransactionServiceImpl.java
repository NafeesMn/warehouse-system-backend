package com.artiselite.warehouse.outbound.service.impl;

import com.artiselite.warehouse.common.dto.ProductReferenceOptionResponse;
import com.artiselite.warehouse.common.dto.ReferenceOptionResponse;
import com.artiselite.warehouse.customer.entity.Customer;
import com.artiselite.warehouse.customer.repository.CustomerRepository;
import com.artiselite.warehouse.exception.BadRequestException;
import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionRequest;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionResponse;
import com.artiselite.warehouse.outbound.dto.response.OutboundFormOptionsResponse;
import com.artiselite.warehouse.outbound.entity.OutboundTransaction;
import com.artiselite.warehouse.outbound.repository.OutboundTransactionRepository;
import com.artiselite.warehouse.outbound.service.OutboundTransactionService;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OutboundTransactionServiceImpl implements OutboundTransactionService {

    private final OutboundTransactionRepository outboundTransactionRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public OutboundTransactionServiceImpl(
            OutboundTransactionRepository outboundTransactionRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository
    ) {
        this.outboundTransactionRepository = outboundTransactionRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<OutboundTransactionResponse> getAllOutboundTransactions() {
        return outboundTransactionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OutboundFormOptionsResponse getOutboundFormOptions() {
        List<ProductReferenceOptionResponse> productOptions = productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(product -> new ProductReferenceOptionResponse(
                        product.getProductId(),
                        product.getSku(),
                        product.getName(),
                        product.getCurrentStock()
                ))
                .toList();

        List<ReferenceOptionResponse> customerOptions = customerRepository.findAllByOrderByCustomerNameAsc().stream()
                .map(customer -> new ReferenceOptionResponse(
                        customer.getCustomerId(),
                        customer.getCustomerName(),
                        customer.getContactPerson()
                ))
                .toList();

        return new OutboundFormOptionsResponse(productOptions, customerOptions);
    }

    @Override
    public OutboundTransactionResponse getOutboundTransactionById(Long outboundId) {
        return toResponse(getRequiredOutbound(outboundId));
    }

    @Override
    @Transactional
    public OutboundTransactionResponse createOutboundTransaction(OutboundTransactionRequest request, String createdByEmail) {
        validateQuantity(request.quantity());

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.productId()));
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.customerId()));
        User createdBy = userRepository.findByEmailIgnoreCase(createdByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + createdByEmail));

        int currentStock = product.getCurrentStock() == null ? 0 : product.getCurrentStock();
        if (currentStock < request.quantity()) {
            throw new BadRequestException(
                    "Insufficient stock for product " + product.getSku()
                            + ". Available: " + currentStock
                            + ", requested: " + request.quantity() + "."
            );
        }

        int updatedStock;
        try {
            updatedStock = Math.subtractExact(currentStock, request.quantity());
        } catch (ArithmeticException exception) {
            throw new BadRequestException("Stock update exceeds the allowed integer range.");
        }

        product.setCurrentStock(updatedStock);
        productRepository.save(product);

        OutboundTransaction outboundTransaction = new OutboundTransaction();
        outboundTransaction.setProduct(product);
        outboundTransaction.setCustomer(customer);
        outboundTransaction.setQuantity(request.quantity());
        outboundTransaction.setShippedDate(request.shippedDate());
        outboundTransaction.setReferenceNo(normalizeOptionalText(request.referenceNo()));
        outboundTransaction.setRemarks(normalizeOptionalText(request.remarks()));
        outboundTransaction.setCreatedBy(createdBy);

        OutboundTransaction saved = outboundTransactionRepository.save(outboundTransaction);
        return toResponse(saved);
    }

    private OutboundTransaction getRequiredOutbound(Long outboundId) {
        return outboundTransactionRepository.findById(outboundId)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound transaction not found: " + outboundId));
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero.");
        }
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private OutboundTransactionResponse toResponse(OutboundTransaction outboundTransaction) {
        return new OutboundTransactionResponse(
                outboundTransaction.getOutboundId(),
                outboundTransaction.getProduct().getProductId(),
                outboundTransaction.getProduct().getName(),
                outboundTransaction.getCustomer().getCustomerId(),
                outboundTransaction.getCustomer().getCustomerName(),
                outboundTransaction.getQuantity(),
                outboundTransaction.getShippedDate(),
                outboundTransaction.getReferenceNo(),
                outboundTransaction.getRemarks(),
                outboundTransaction.getCreatedBy().getUserId(),
                outboundTransaction.getCreatedBy().getFullName(),
                outboundTransaction.getCreatedAt()
        );
    }
}