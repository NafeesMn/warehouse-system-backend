package com.artiselite.warehouse.supplier.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.supplier.dto.request.CreateSupplierRequest;
import com.artiselite.warehouse.supplier.dto.response.SupplierResponse;
import com.artiselite.warehouse.supplier.service.SupplierService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suppliers")
@PreAuthorize("hasRole('MANAGER')")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ApiResponse<List<SupplierResponse>> getSuppliers() {
        return ApiResponse.success("Suppliers loaded successfully.", supplierService.getAllSuppliers());
    }

    @GetMapping("/{supplierId}")
    public ApiResponse<SupplierResponse> getSupplier(@PathVariable Long supplierId) {
        return ApiResponse.success("Supplier loaded successfully.", supplierService.getSupplierById(supplierId));
    }

    @PostMapping
    public ApiResponse<SupplierResponse> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        return ApiResponse.success("Supplier created successfully.", supplierService.createSupplier(request));
    }

    @PutMapping("/{supplierId}")
    public ApiResponse<SupplierResponse> updateSupplier(
            @PathVariable Long supplierId,
            @Valid @RequestBody CreateSupplierRequest request
    ) {
        return ApiResponse.success(
                "Supplier updated successfully.",
                supplierService.updateSupplier(supplierId, request)
        );
    }
}