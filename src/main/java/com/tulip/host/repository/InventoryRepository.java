package com.tulip.host.repository;

import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.ProductCatalog;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> stockReport();

    List<Inventory> stockReportWithIndex();

    List<Inventory> findByProductOrderByCreatedDateAsc(ProductCatalog product);

    List<Inventory> findByProductAndActiveTrue(ProductCatalog product);

    List<Inventory> findAllByProduct_IdOrderByCreatedDateDesc(Long productId);

    List<Inventory> findLatestInventoryByProductCatalog();

    Map<ProductCatalog, List<Inventory>> findLatestInventoryByProductCatalogGrouped();
}
