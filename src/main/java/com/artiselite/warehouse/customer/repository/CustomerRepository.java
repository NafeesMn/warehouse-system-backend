package com.artiselite.warehouse.customer.repository;

import com.artiselite.warehouse.customer.entity.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findAllByOrderByCustomerNameAsc();
}
