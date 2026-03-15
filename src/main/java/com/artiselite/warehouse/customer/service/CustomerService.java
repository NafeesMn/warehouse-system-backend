package com.artiselite.warehouse.customer.service;

import com.artiselite.warehouse.customer.dto.CustomerRequest;
import com.artiselite.warehouse.customer.dto.CustomerResponse;
import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getAllCustomers();

    CustomerResponse getCustomerById(Long customerId);

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse updateCustomer(Long customerId, CustomerRequest request);
}
