package com.artiselite.warehouse.supplier.service;

import com.artiselite.warehouse.supplier.dto.request.CreateSupplierRequest;
import com.artiselite.warehouse.supplier.dto.response.SupplierResponse;
import java.util.List;

public interface SupplierService {

    List<SupplierResponse> getAllSuppliers();

    SupplierResponse getSupplierById(Long supplierId);

    SupplierResponse createSupplier(CreateSupplierRequest request);

    SupplierResponse updateSupplier(Long supplierId, CreateSupplierRequest request);
}