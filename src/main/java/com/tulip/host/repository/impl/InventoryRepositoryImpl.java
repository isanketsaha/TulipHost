package com.tulip.host.repository.impl;

import static com.tulip.host.config.Constants.CATEGORY_BOOK;
import static com.tulip.host.config.Constants.CATEGORY_PLACEHOLDER;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.repository.InventoryRepository;

import jakarta.persistence.EntityManager;

public class InventoryRepositoryImpl extends BaseRepositoryImpl<Inventory, Long> implements InventoryRepository {

    protected InventoryRepositoryImpl(EntityManager em) {
        super(Inventory.class, em);
    }

    @Override
    public List<Inventory> stockReport() {
        return jpaQueryFactory
            .selectFrom(INVENTORY)
            .innerJoin(INVENTORY.product(), PRODUCT_CATALOG)
            .leftJoin(PRODUCT_CATALOG.std(), CLASS_DETAIL)
            .where(
                PRODUCT_CATALOG.active
                    .eq(true)
                    .and(PRODUCT_CATALOG.category.ne(CATEGORY_PLACEHOLDER)
                        .and(PRODUCT_CATALOG.category.ne(CATEGORY_BOOK)))
            )
            .fetch();
    }

    @Override
    public List<Inventory> stockReportWithIndex() {
        return jpaQueryFactory
                .selectFrom(INVENTORY)
                .innerJoin(INVENTORY.product(), PRODUCT_CATALOG)
                .leftJoin(PRODUCT_CATALOG.std(), CLASS_DETAIL)
                .where(
                        PRODUCT_CATALOG.active
                                .eq(true)
                                .and(PRODUCT_CATALOG.category.ne(CATEGORY_PLACEHOLDER)
                                        .and(PRODUCT_CATALOG.category.ne(CATEGORY_BOOK))))
                .orderBy(PRODUCT_CATALOG.itemName.asc())
                .fetch();
    }

    @Override
    public List<Inventory> findByProductOrderByCreatedDateAsc(com.tulip.host.domain.ProductCatalog product) {
        return jpaQueryFactory
                .selectFrom(INVENTORY)
                .where(INVENTORY.product().eq(product))
                .orderBy(INVENTORY.createdDate.asc())
                .fetch();
    }

    @Override
    public List<Inventory> findLatestInventoryByProductCatalog() {
        return jpaQueryFactory
                .selectFrom(INVENTORY)
                .innerJoin(INVENTORY.product(), PRODUCT_CATALOG)
                .fetchJoin()
                .leftJoin(PRODUCT_CATALOG.std(), CLASS_DETAIL)
                .fetchJoin()
                .where(
                        PRODUCT_CATALOG.active
                                .eq(true)
                                .and(PRODUCT_CATALOG.category.ne(CATEGORY_PLACEHOLDER)
                                        .and(PRODUCT_CATALOG.category.ne(CATEGORY_BOOK)))
                                .and(INVENTORY.active.eq(true)))
                .orderBy(PRODUCT_CATALOG.itemName.asc(), INVENTORY.createdDate.desc())
                .fetch();
    }

    @Override
    public Map<ProductCatalog, List<Inventory>> findLatestInventoryByProductCatalogGrouped() {
        List<Inventory> inventories = findLatestInventoryByProductCatalog();
        return inventories.stream()
                .collect(Collectors.groupingBy(Inventory::getProduct));
    }
}
