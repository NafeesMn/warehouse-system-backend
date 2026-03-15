package com.artiselite.warehouse.supplier.service;

import com.artiselite.warehouse.supplier.dto.SupplierRequest;
import com.artiselite.warehouse.supplier.dto.SupplierResponse;
import java.util.List;

public interface SupplierService {

    List<SupplierResponse> getAllSuppliers();

    SupplierResponse getSupplierById(Long supplierId);

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse updateSupplier(Long supplierId, SupplierRequest request);
}
