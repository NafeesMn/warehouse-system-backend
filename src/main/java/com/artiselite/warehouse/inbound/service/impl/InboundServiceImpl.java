package com.artiselite.warehouse.inbound.service.impl;

import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.common.dto.ProductReferenceOptionResponse;
import com.artiselite.warehouse.common.dto.ReferenceOptionResponse;
import com.artiselite.warehouse.exception.BadRequestException;
import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.inbound.dto.request.CreateInboundRequest;
import com.artiselite.warehouse.inbound.dto.response.InboundFormOptionsResponse;
import com.artiselite.warehouse.inbound.dto.response.InboundListItemResponse;
import com.artiselite.warehouse.inbound.dto.response.InboundResponse;
import com.artiselite.warehouse.inbound.entity.InboundTransaction;
import com.artiselite.warehouse.inbound.mapper.InboundMapper;
import com.artiselite.warehouse.inbound.repository.InboundTransactionRepository;
import com.artiselite.warehouse.inbound.service.InboundService;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.supplier.entity.Supplier;
import com.artiselite.warehouse.supplier.repository.SupplierRepository;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InboundServiceImpl implements InboundService {

    private static final int MAX_PAGE_SIZE = 100;

    private final InboundTransactionRepository inboundTransactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final InboundMapper inboundMapper;

    public InboundServiceImpl(
            InboundTransactionRepository inboundTransactionRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            UserRepository userRepository,
            InboundMapper inboundMapper
    ) {
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.inboundMapper = inboundMapper;
    }

    @Override
    @Transactional
    public InboundResponse createInbound(CreateInboundRequest request, String createdByEmail) {
        validateQuantity(request.quantity());

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.productId()));
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + request.supplierId()));
        User createdBy = userRepository.findByEmailIgnoreCase(createdByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + createdByEmail));

        int updatedStock;
        try {
            updatedStock = Math.addExact(product.getCurrentStock(), request.quantity());
        } catch (ArithmeticException exception) {
            throw new BadRequestException("Stock update exceeds the allowed integer range.");
        }

        product.setCurrentStock(updatedStock);
        productRepository.save(product);

        InboundTransaction inboundTransaction = new InboundTransaction();
        inboundTransaction.setProduct(product);
        inboundTransaction.setSupplier(supplier);
        inboundTransaction.setQuantity(request.quantity());
        inboundTransaction.setReceivedDate(request.receivedDate());
        inboundTransaction.setReferenceNo(normalizeOptionalText(request.referenceNo()));
        inboundTransaction.setRemarks(normalizeOptionalText(request.remarks()));
        inboundTransaction.setCreatedBy(createdBy);
        inboundTransaction.setStockAfterUpdate(updatedStock);

        return inboundMapper.toResponse(inboundTransactionRepository.save(inboundTransaction));
    }

    @Override
    public PagedResponse<InboundListItemResponse> getInbounds(
            Long productId,
            Long supplierId,
            int page,
            int size,
            String sortDirection
    ) {
        Pageable pageable = buildPageable(page, size, sortDirection);
        Page<InboundListItemResponse> result = inboundTransactionRepository
                .findAllByFilters(productId, supplierId, pageable)
                .map(inboundMapper::toListItemResponse);
        return PagedResponse.from(result);
    }

    @Override
    public InboundFormOptionsResponse getInboundFormOptions() {
        List<ProductReferenceOptionResponse> productOptions = productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(product -> new ProductReferenceOptionResponse(
                        product.getProductId(),
                        product.getSku(),
                        product.getName(),
                        product.getCurrentStock()
                ))
                .toList();

        List<ReferenceOptionResponse> supplierOptions = supplierRepository.findAllByOrderBySupplierNameAsc().stream()
                .map(supplier -> new ReferenceOptionResponse(
                        supplier.getSupplierId(),
                        supplier.getSupplierName(),
                        supplier.getContactPerson()
                ))
                .toList();

        return new InboundFormOptionsResponse(productOptions, supplierOptions);
    }

    @Override
    public InboundResponse getInboundById(Long inboundId) {
        return inboundMapper.toResponse(getRequiredInbound(inboundId));
    }

    private InboundTransaction getRequiredInbound(Long inboundId) {
        return inboundTransactionRepository.findById(inboundId)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound transaction not found: " + inboundId));
    }

    private Pageable buildPageable(int page, int size, String sortDirection) {
        if (page < 0) {
            throw new BadRequestException("Page must be zero or greater.");
        }
        if (size <= 0 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("Size must be between 1 and 100.");
        }

        Sort.Direction direction;
        try {
            direction = sortDirection == null || sortDirection.isBlank()
                    ? Sort.Direction.DESC
                    : Sort.Direction.fromString(sortDirection.trim());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Sort direction must be ASC or DESC.");
        }

        return PageRequest.of(page, size, Sort.by(direction, "receivedDate", "createdAt", "inboundId"));
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
}