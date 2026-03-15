package com.artiselite.warehouse.customer.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.customer.dto.CustomerRequest;
import com.artiselite.warehouse.customer.dto.CustomerResponse;
import com.artiselite.warehouse.customer.service.CustomerService;
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
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ApiResponse<List<CustomerResponse>> getCustomers() {
        return ApiResponse.success("Customers loaded successfully.", customerService.getAllCustomers());
    }

    @GetMapping("/{customerId}")
    public ApiResponse<CustomerResponse> getCustomer(@PathVariable Long customerId) {
        return ApiResponse.success("Customer loaded successfully.", customerService.getCustomerById(customerId));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        return ApiResponse.success("Customer created successfully.", customerService.createCustomer(request));
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<CustomerResponse> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequest request
    ) {
        return ApiResponse.success(
                "Customer updated successfully.",
                customerService.updateCustomer(customerId, request)
        );
    }
}
