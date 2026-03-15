package com.artiselite.warehouse.customer.service.impl;

import com.artiselite.warehouse.customer.dto.CustomerRequest;
import com.artiselite.warehouse.customer.dto.CustomerResponse;
import com.artiselite.warehouse.customer.entity.Customer;
import com.artiselite.warehouse.customer.repository.CustomerRepository;
import com.artiselite.warehouse.customer.service.CustomerService;
import com.artiselite.warehouse.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAllByOrderByCustomerNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        return toResponse(getRequiredCustomer(customerId));
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        applyRequest(customer, request);
        return toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long customerId, CustomerRequest request) {
        Customer customer = getRequiredCustomer(customerId);
        applyRequest(customer, request);
        return toResponse(customerRepository.save(customer));
    }

    private Customer getRequiredCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
    }

    private void applyRequest(Customer customer, CustomerRequest request) {
        customer.setCustomerName(request.customerName().trim());
        customer.setContactPerson(request.contactPerson());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
        customer.setAddress(request.address());
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getContactPerson(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
