package com.tulip.host.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.ProductCatalog;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> stockReport();

    List<Inventory> stockReportWithIndex();

    List<Inventory> findByProductOrderByCreatedDateAsc(ProductCatalog product);
}
