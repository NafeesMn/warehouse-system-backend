package com.artiselite.warehouse.outbound.service.impl;

import com.artiselite.warehouse.customer.entity.Customer;
import com.artiselite.warehouse.customer.repository.CustomerRepository;
import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionRequest;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionResponse;
import com.artiselite.warehouse.outbound.entity.OutboundTransaction;
import com.artiselite.warehouse.outbound.repository.OutboundTransactionRepository;
import com.artiselite.warehouse.outbound.service.OutboundTransactionService;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
        return outboundTransactionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OutboundTransactionResponse getOutboundTransactionById(Long outboundId) {
        return toResponse(getRequiredOutbound(outboundId));
    }

    @Override
    @Transactional
    public OutboundTransactionResponse createOutboundTransaction(OutboundTransactionRequest request, String createdByEmail) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.productId()));
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.customerId()));
        User createdBy = userRepository.findByEmailIgnoreCase(createdByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + createdByEmail));

        OutboundTransaction outboundTransaction = new OutboundTransaction();
        outboundTransaction.setProduct(product);
        outboundTransaction.setCustomer(customer);
        outboundTransaction.setQuantity(request.quantity());
        outboundTransaction.setShippedDate(request.shippedDate());
        outboundTransaction.setReferenceNo(request.referenceNo());
        outboundTransaction.setRemarks(request.remarks());
        outboundTransaction.setCreatedBy(createdBy);

        OutboundTransaction saved = outboundTransactionRepository.save(outboundTransaction);

        // TODO Phase 4: enforce stock sufficiency and apply stock decrement.
        return toResponse(saved);
    }

    private OutboundTransaction getRequiredOutbound(Long outboundId) {
        return outboundTransactionRepository.findById(outboundId)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound transaction not found: " + outboundId));
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
