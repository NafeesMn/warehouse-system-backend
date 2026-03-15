package com.artiselite.warehouse.inbound.repository;

import com.artiselite.warehouse.inbound.entity.InboundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboundTransactionRepository extends JpaRepository<InboundTransaction, Long> {
}
