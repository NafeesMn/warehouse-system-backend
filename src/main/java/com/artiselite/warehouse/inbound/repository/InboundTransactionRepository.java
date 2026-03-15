package com.artiselite.warehouse.inbound.repository;

import com.artiselite.warehouse.inbound.entity.InboundTransaction;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InboundTransactionRepository extends JpaRepository<InboundTransaction, Long> {

    @Override
    @EntityGraph(attributePaths = {"product", "supplier", "createdBy"})
    Optional<InboundTransaction> findById(Long inboundId);

    @EntityGraph(attributePaths = {"product", "supplier", "createdBy"})
    @Query("""
            select i from InboundTransaction i
            where (:productId is null or i.product.productId = :productId)
              and (:supplierId is null or i.supplier.supplierId = :supplierId)
            """)
    Page<InboundTransaction> findAllByFilters(
            @Param("productId") Long productId,
            @Param("supplierId") Long supplierId,
            Pageable pageable
    );
}