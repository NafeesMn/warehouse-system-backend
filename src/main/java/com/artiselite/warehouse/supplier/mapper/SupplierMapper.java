package com.artiselite.warehouse.supplier.mapper;

import com.artiselite.warehouse.supplier.dto.request.CreateSupplierRequest;
import com.artiselite.warehouse.supplier.dto.response.SupplierResponse;
import com.artiselite.warehouse.supplier.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(CreateSupplierRequest request) {
        Supplier supplier = new Supplier();
        apply(supplier, request);
        return supplier;
    }

    public void updateEntity(Supplier supplier, CreateSupplierRequest request) {
        apply(supplier, request);
    }

    public SupplierResponse toResponse(Supplier supplier) {
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

    private void apply(Supplier supplier, CreateSupplierRequest request) {
        supplier.setSupplierName(normalizeRequiredText(request.supplierName()));
        supplier.setContactPerson(normalizeOptionalText(request.contactPerson()));
        supplier.setPhone(normalizeOptionalText(request.phone()));
        supplier.setEmail(normalizeOptionalText(request.email()));
        supplier.setAddress(normalizeOptionalText(request.address()));
    }

    private String normalizeRequiredText(String value) {
        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}