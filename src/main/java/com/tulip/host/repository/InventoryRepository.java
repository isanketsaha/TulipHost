package com.tulip.host.repository;

import com.tulip.host.domain.Inventory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> stockReport();
}
