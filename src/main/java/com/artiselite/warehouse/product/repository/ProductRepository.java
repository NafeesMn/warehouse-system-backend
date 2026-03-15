package com.artiselite.warehouse.product.repository;

import com.artiselite.warehouse.product.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndProductIdNot(String sku, Long productId);

    @Query("""
            select p from Product p
            where lower(p.sku) like lower(concat('%', :keyword, '%'))
               or lower(p.name) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(p.description, '')) like lower(concat('%', :keyword, '%'))
            """)
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Product> findByTagsContainingIgnoreCase(String tag, Pageable pageable);

    @Query("""
            select p from Product p
            where (
                lower(p.sku) like lower(concat('%', :keyword, '%'))
                or lower(p.name) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(p.description, '')) like lower(concat('%', :keyword, '%'))
            )
            and lower(coalesce(p.tags, '')) like lower(concat('%', :tag, '%'))
            """)
    Page<Product> searchByKeywordAndTag(
            @Param("keyword") String keyword,
            @Param("tag") String tag,
            Pageable pageable
    );

    @Query("select count(p) from Product p where p.currentStock <= p.reorderLevel")
    long countLowStockProducts();
}