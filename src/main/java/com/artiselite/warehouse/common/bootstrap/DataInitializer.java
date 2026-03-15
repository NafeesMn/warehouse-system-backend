package com.artiselite.warehouse.common.bootstrap;

import com.artiselite.warehouse.common.config.AppSeedProperties;
import com.artiselite.warehouse.customer.entity.Customer;
import com.artiselite.warehouse.customer.repository.CustomerRepository;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.role.entity.Role;
import com.artiselite.warehouse.role.repository.RoleRepository;
import com.artiselite.warehouse.supplier.entity.Supplier;
import com.artiselite.warehouse.supplier.repository.SupplierRepository;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner seedUsers(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AppSeedProperties seedProperties
    ) {
        return args -> {
            seedUserIfMissing(
                    roleRepository,
                    userRepository,
                    passwordEncoder,
                    seedProperties.getManagerEmail(),
                    seedProperties.getManagerPassword(),
                    seedProperties.getManagerFullName(),
                    "MANAGER"
            );
            seedUserIfMissing(
                    roleRepository,
                    userRepository,
                    passwordEncoder,
                    seedProperties.getOperatorEmail(),
                    seedProperties.getOperatorPassword(),
                    seedProperties.getOperatorFullName(),
                    "OPERATOR"
            );
        };
    }

    @Bean
    public ApplicationRunner seedDemoDomainData(
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            CustomerRepository customerRepository
    ) {
        return args -> {
            seedProductsIfMissing(productRepository);
            seedSuppliersIfMissing(supplierRepository);
            seedCustomersIfMissing(customerRepository);
        };
    }

    private void seedUserIfMissing(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String password,
            String fullName,
            String roleName
    ) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        Role role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new IllegalStateException("Required role not found: " + roleName));

        User user = new User();
        user.setEmail(email.toLowerCase());
        user.setFullName(fullName);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setIsActive(true);
        userRepository.save(user);
    }

    private void seedProductsIfMissing(ProductRepository productRepository) {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.saveAll(List.of(
                buildProduct("SKU-1001", "Ceramic Vase", "home-decor,fragile", new BigDecimal("79.90"), 42, 10),
                buildProduct("SKU-1002", "Oak Side Table", "furniture,wood", new BigDecimal("149.00"), 12, 5),
                buildProduct("SKU-1003", "Linen Cushion Set", "textile,soft-furnishing", new BigDecimal("39.50"), 28, 8),
                buildProduct("SKU-1004", "Pendant Lamp", "lighting,modern", new BigDecimal("99.99"), 9, 4)
        ));
    }

    private void seedSuppliersIfMissing(SupplierRepository supplierRepository) {
        if (supplierRepository.count() > 0) {
            return;
        }

        supplierRepository.saveAll(List.of(
                buildSupplier("Artiselite Home Goods", "Nadia Tan", "+60-12-3456789", "supply@artiselite.local", "Lot 12, Jalan Perdagangan"),
                buildSupplier("Northern Lighting Co.", "Hafiz Rahman", "+60-13-2345678", "lighting@northern.local", "88 Industrial Park Avenue")
        ));
    }

    private void seedCustomersIfMissing(CustomerRepository customerRepository) {
        if (customerRepository.count() > 0) {
            return;
        }

        customerRepository.saveAll(List.of(
                buildCustomer("Bluebird Retail", "Amira Salleh", "+60-14-1112233", "procurement@bluebird.local", "15 Central Mall"),
                buildCustomer("Studio Habitat", "Jon Lim", "+60-18-9998877", "buyer@studiohabitat.local", "22 Riverside Market")
        ));
    }

    private Product buildProduct(
            String sku,
            String name,
            String tags,
            BigDecimal unitPrice,
            int currentStock,
            int reorderLevel
    ) {
        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setDescription(name + " sample product for local review.");
        product.setTags(tags);
        product.setUnitPrice(unitPrice);
        product.setCurrentStock(currentStock);
        product.setReorderLevel(reorderLevel);
        return product;
    }

    private Supplier buildSupplier(
            String name,
            String contactPerson,
            String phone,
            String email,
            String address
    ) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(name);
        supplier.setContactPerson(contactPerson);
        supplier.setPhone(phone);
        supplier.setEmail(email);
        supplier.setAddress(address);
        return supplier;
    }

    private Customer buildCustomer(
            String name,
            String contactPerson,
            String phone,
            String email,
            String address
    ) {
        Customer customer = new Customer();
        customer.setCustomerName(name);
        customer.setContactPerson(contactPerson);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);
        return customer;
    }
}