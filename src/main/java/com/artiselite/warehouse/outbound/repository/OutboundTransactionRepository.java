package com.artiselite.warehouse.outbound.repository;

import com.artiselite.warehouse.outbound.entity.OutboundTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundTransactionRepository extends JpaRepository<OutboundTransaction, Long> {

    @Override
    @EntityGraph(attributePaths = {"product", "customer", "createdBy"})
    Optional<OutboundTransaction> findById(Long outboundId);

    @EntityGraph(attributePaths = {"product", "customer", "createdBy"})
    List<OutboundTransaction> findAllByOrderByCreatedAtDesc();
}