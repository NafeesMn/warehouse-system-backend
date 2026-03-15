package com.artiselite.warehouse.product.repository;

import com.artiselite.warehouse.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCase(String sku);

    @Query("select count(p) from Product p where p.currentStock <= p.reorderLevel")
    long countLowStockProducts();
}
