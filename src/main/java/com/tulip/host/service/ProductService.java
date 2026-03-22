package com.tulip.host.service;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.mapper.InventoryMapper;
import com.tulip.host.mapper.ProductCatalogMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.repository.ProductCatalogPagedRepository;
import com.tulip.host.web.rest.vm.dataload.ProductLoadVM;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductCatalogMapper productCatalogMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryRepository inventoryRepository;
    private final ClassDetailRepository classDetailRepository;

    private final ProductCatalogPagedRepository productCatalogPagedRepository;

    @Transactional
    public void loadProducts(List<ProductLoadVM> products) {
        // Deduplicate ProductCatalog within this batch by itemName+tag
        Map<String, ProductCatalog> catalogCache = new HashMap<>();

        List<Inventory> inventoryList = products
            .stream()
            .map(item -> {
                String key = item.getItemName().toUpperCase() + "|" + item.getTag();
                ProductCatalog productCatalog = catalogCache.computeIfAbsent(key, k -> {
                    ProductCatalog pc = productCatalogMapper.toModel(item);
                    if (item.getSession() != null && item.getClassDetail() != null) {
                        Long sessionId = Long.parseLong(item.getSession());
                        ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(sessionId, item.getClassDetail());
                        pc.setStd(classDetail);
                    }
                    return pc;
                });

                Inventory inventory = inventoryMapper.toModel(item);
                inventory.setProduct(productCatalog);
                return inventory;
            })
            .collect(Collectors.toList());

        // Save all inventory entries
        inventoryRepository.saveAllAndFlush(inventoryList);
        log.info("Loaded {} products with inventory entries", inventoryList.size());
    }

    public void updateProductVisibility(long id, boolean flag) {
        productCatalogPagedRepository.updateProductVisibility(id, flag);
    }
}
