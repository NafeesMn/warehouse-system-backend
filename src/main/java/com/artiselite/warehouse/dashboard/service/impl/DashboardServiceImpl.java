package com.artiselite.warehouse.dashboard.service.impl;

import com.artiselite.warehouse.customer.repository.CustomerRepository;
import com.artiselite.warehouse.dashboard.dto.DashboardSummaryResponse;
import com.artiselite.warehouse.dashboard.service.DashboardService;
import com.artiselite.warehouse.inbound.repository.InboundTransactionRepository;
import com.artiselite.warehouse.outbound.repository.OutboundTransactionRepository;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.supplier.repository.SupplierRepository;
import com.artiselite.warehouse.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final InboundTransactionRepository inboundTransactionRepository;
    private final OutboundTransactionRepository outboundTransactionRepository;

    public DashboardServiceImpl(
            UserRepository userRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            CustomerRepository customerRepository,
            InboundTransactionRepository inboundTransactionRepository,
            OutboundTransactionRepository outboundTransactionRepository
    ) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.customerRepository = customerRepository;
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.outboundTransactionRepository = outboundTransactionRepository;
    }

    @Override
    public DashboardSummaryResponse getSummary() {
        return new DashboardSummaryResponse(
                userRepository.count(),
                productRepository.count(),
                supplierRepository.count(),
                customerRepository.count(),
                inboundTransactionRepository.count(),
                outboundTransactionRepository.count(),
                productRepository.countLowStockProducts()
        );
    }
}
