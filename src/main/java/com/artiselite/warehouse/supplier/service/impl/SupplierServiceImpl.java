package com.artiselite.warehouse.supplier.service.impl;

import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.supplier.dto.request.CreateSupplierRequest;
import com.artiselite.warehouse.supplier.dto.response.SupplierResponse;
import com.artiselite.warehouse.supplier.entity.Supplier;
import com.artiselite.warehouse.supplier.mapper.SupplierMapper;
import com.artiselite.warehouse.supplier.repository.SupplierRepository;
import com.artiselite.warehouse.supplier.service.SupplierService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAllByOrderBySupplierNameAsc().stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    @Override
    public SupplierResponse getSupplierById(Long supplierId) {
        return supplierMapper.toResponse(getRequiredSupplier(supplierId));
    }

    @Override
    @Transactional
    public SupplierResponse createSupplier(CreateSupplierRequest request) {
        Supplier supplier = supplierMapper.toEntity(request);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(Long supplierId, CreateSupplierRequest request) {
        Supplier supplier = getRequiredSupplier(supplierId);
        supplierMapper.updateEntity(supplier, request);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    private Supplier getRequiredSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierId));
    }
}