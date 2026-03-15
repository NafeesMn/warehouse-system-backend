package com.artiselite.warehouse.supplier.service.impl;

import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.supplier.dto.SupplierRequest;
import com.artiselite.warehouse.supplier.dto.SupplierResponse;
import com.artiselite.warehouse.supplier.entity.Supplier;
import com.artiselite.warehouse.supplier.repository.SupplierRepository;
import com.artiselite.warehouse.supplier.service.SupplierService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAllByOrderBySupplierNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SupplierResponse getSupplierById(Long supplierId) {
        return toResponse(getRequiredSupplier(supplierId));
    }

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        Supplier supplier = new Supplier();
        applyRequest(supplier, request);
        return toResponse(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(Long supplierId, SupplierRequest request) {
        Supplier supplier = getRequiredSupplier(supplierId);
        applyRequest(supplier, request);
        return toResponse(supplierRepository.save(supplier));
    }

    private Supplier getRequiredSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierId));
    }

    private void applyRequest(Supplier supplier, SupplierRequest request) {
        supplier.setSupplierName(request.supplierName().trim());
        supplier.setContactPerson(request.contactPerson());
        supplier.setPhone(request.phone());
        supplier.setEmail(request.email());
        supplier.setAddress(request.address());
    }

    private SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(
                supplier.getSupplierId(),
                supplier.getSupplierName(),
                supplier.getContactPerson(),
                supplier.getPhone(),
                supplier.getEmail(),
                supplier.getAddress(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
        );
    }
}
