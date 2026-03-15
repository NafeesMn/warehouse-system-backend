package com.artiselite.warehouse.supplier.repository;

import com.artiselite.warehouse.supplier.entity.Supplier;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findAllByOrderBySupplierNameAsc();
}
