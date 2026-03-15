package com.artiselite.warehouse.outbound.repository;

import com.artiselite.warehouse.outbound.entity.OutboundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundTransactionRepository extends JpaRepository<OutboundTransaction, Long> {
}
