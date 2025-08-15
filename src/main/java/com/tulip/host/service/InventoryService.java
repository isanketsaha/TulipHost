package com.tulip.host.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Find available inventory for a product in FIFO order
     */
    public List<Inventory> findAvailableInventoryForProduct(ProductCatalog product) {
        return inventoryRepository.findByProductOrderByCreatedDateAsc(product);
    }

    /**
     * Calculate available quantity for a specific inventory batch
     */
    public int calculateAvailableQuantity(Inventory inventory) {
        return inventory.getQty() - inventory.getPurchaseLineItems()
                .stream()
                .mapToInt(item -> item.getQty())
                .sum();
    }

    /**
     * Get total available quantity for a product across all inventory batches
     */
    public int getTotalAvailableQuantity(ProductCatalog product) {
        return product.getInventories().stream()
                .mapToInt(this::calculateAvailableQuantity)
                .sum();
    }
}
