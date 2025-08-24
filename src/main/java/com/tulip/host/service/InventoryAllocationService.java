package com.tulip.host.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.domain.PurchaseLineItem;
import com.tulip.host.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryAllocationService {

    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    /**
     * Allocate inventory to purchase line item using FIFO strategy
     */
    @Transactional
    public void allocateInventoryToPurchaseItem(PurchaseLineItem purchaseLineItem) {
        ProductCatalog product = purchaseLineItem.getProduct();
        int requiredQty = purchaseLineItem.getQty();

        // Find available inventory for this product (FIFO order)
        List<Inventory> availableInventory = inventoryRepository.findByProductOrderByCreatedDateAsc(product);

        if (availableInventory.isEmpty()) {
            throw new RuntimeException("No inventory available for product: " + product.getItemName());
        }

        // Calculate total available quantity
        int totalAvailable = inventoryService.getTotalAvailableQuantity(product);
        if (totalAvailable < requiredQty) {
            throw new RuntimeException("Insufficient stock for product: " + product.getItemName() +
                    ". Required: " + requiredQty + ", Available: " + totalAvailable);
        }

        // Use FIFO allocation
        Inventory allocatedInventory = findInventoryWithFIFO(availableInventory, requiredQty);

        if (allocatedInventory == null) {
            throw new RuntimeException("Unable to allocate inventory for product: " + product.getItemName());
        }

        // Assign the inventory to the purchase line item
        purchaseLineItem.setInventory(allocatedInventory);
    }

    /**
     * Find inventory using FIFO (First In, First Out) strategy
     */
    private Inventory findInventoryWithFIFO(List<Inventory> availableInventory, int requiredQty) {
        for (Inventory inventory : availableInventory) {
            int availableQty = inventoryService.calculateAvailableQuantity(inventory);
            if (availableQty >= requiredQty) {
                return inventory;
            }
        }
        return null;
    }

    /**
     * Calculate the expected price for a product (same logic as UI)
     */
    public double calculateExpectedPrice(ProductCatalog product) {
        // Get the highest price from all inventory batches
        if (product.getPrice() != null) {
            return product.getPrice();
        }

        return product.getInventories().stream()
                .mapToDouble(Inventory::getUnitPrice)
                .max()
                .orElse(product.getPrice());
    }
}
