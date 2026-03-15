package com.artiselite.warehouse.inbound.service.impl;

import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.inbound.dto.InboundTransactionRequest;
import com.artiselite.warehouse.inbound.dto.InboundTransactionResponse;
import com.artiselite.warehouse.inbound.entity.InboundTransaction;
import com.artiselite.warehouse.inbound.repository.InboundTransactionRepository;
import com.artiselite.warehouse.inbound.service.InboundTransactionService;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.supplier.entity.Supplier;
import com.artiselite.warehouse.supplier.repository.SupplierRepository;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InboundTransactionServiceImpl implements InboundTransactionService {

    private final InboundTransactionRepository inboundTransactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    public InboundTransactionServiceImpl(
            InboundTransactionRepository inboundTransactionRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            UserRepository userRepository
    ) {
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<InboundTransactionResponse> getAllInboundTransactions() {
        return inboundTransactionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public InboundTransactionResponse getInboundTransactionById(Long inboundId) {
        return toResponse(getRequiredInbound(inboundId));
    }

    @Override
    @Transactional
    public InboundTransactionResponse createInboundTransaction(InboundTransactionRequest request, String createdByEmail) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.productId()));
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + request.supplierId()));
        User createdBy = userRepository.findByEmailIgnoreCase(createdByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + createdByEmail));

        InboundTransaction inboundTransaction = new InboundTransaction();
        inboundTransaction.setProduct(product);
        inboundTransaction.setSupplier(supplier);
        inboundTransaction.setQuantity(request.quantity());
        inboundTransaction.setReceivedDate(request.receivedDate());
        inboundTransaction.setReferenceNo(request.referenceNo());
        inboundTransaction.setRemarks(request.remarks());
        inboundTransaction.setCreatedBy(createdBy);

        InboundTransaction saved = inboundTransactionRepository.save(inboundTransaction);

        // TODO Phase 3: apply stock increment and richer inbound validation rules.
        return toResponse(saved);
    }

    private InboundTransaction getRequiredInbound(Long inboundId) {
        return inboundTransactionRepository.findById(inboundId)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound transaction not found: " + inboundId));
    }

    private InboundTransactionResponse toResponse(InboundTransaction inboundTransaction) {
        return new InboundTransactionResponse(
                inboundTransaction.getInboundId(),
                inboundTransaction.getProduct().getProductId(),
                inboundTransaction.getProduct().getName(),
                inboundTransaction.getSupplier().getSupplierId(),
                inboundTransaction.getSupplier().getSupplierName(),
                inboundTransaction.getQuantity(),
                inboundTransaction.getReceivedDate(),
                inboundTransaction.getReferenceNo(),
                inboundTransaction.getRemarks(),
                inboundTransaction.getCreatedBy().getUserId(),
                inboundTransaction.getCreatedBy().getFullName(),
                inboundTransaction.getCreatedAt()
        );
    }
}
