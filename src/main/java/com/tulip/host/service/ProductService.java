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
import java.util.List;
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
        List<Inventory> inventoryList = products
            .stream()
            .map(item -> {
                ProductCatalog productCatalog = productCatalogMapper.toModel(item);
                if (item.getSession() != null && item.getClassDetail() != null) {
                    try {
                        Long sessionId = Long.parseLong(item.getSession());
                        ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(sessionId, item.getClassDetail());
                        productCatalog.setStd(classDetail);
                    } catch (NumberFormatException e) {
                        log.warn("Session value '{}' is not a numeric session ID, skipping class detail lookup", item.getSession());
                    }
                }
                Inventory inventory = inventoryMapper.toModel(item);
                inventory.setProduct(productCatalog);
                return inventory;
            })
            .collect(Collectors.toList());

        inventoryRepository.saveAllAndFlush(inventoryList);
        log.info("Loaded {} products with inventory entries", inventoryList.size());
    }

    public void updateProductVisibility(long id, boolean flag) {
        productCatalogPagedRepository.updateProductVisibility(id, flag);
    }
}
