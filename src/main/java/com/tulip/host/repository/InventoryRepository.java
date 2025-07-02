package com.tulip.host.repository;

import com.tulip.host.domain.Inventory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> stockReport();

    List<Inventory> stockReportWithIndex();
}
