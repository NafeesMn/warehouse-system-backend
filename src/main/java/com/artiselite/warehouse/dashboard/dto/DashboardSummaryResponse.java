package com.artiselite.warehouse.dashboard.dto;

public record DashboardSummaryResponse(
        long totalUsers,
        long totalProducts,
        long totalSuppliers,
        long totalCustomers,
        long totalInboundTransactions,
        long totalOutboundTransactions,
        long lowStockProducts
) {
}
