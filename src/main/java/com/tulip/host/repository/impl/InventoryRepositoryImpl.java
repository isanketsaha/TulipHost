package com.tulip.host.repository.impl;

import com.tulip.host.domain.Inventory;
import com.tulip.host.repository.InventoryRepository;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

public class InventoryRepositoryImpl extends BaseRepositoryImpl<Inventory, Long> implements InventoryRepository {

    protected InventoryRepositoryImpl(EntityManager em) {
        super(Inventory.class, em);
    }

    @Override
    public List<Inventory> stockReport(Long sessionId) {
        List<Inventory> inventoryList = jpaQueryFactory
            .selectFrom(INVENTORY)
            .innerJoin(INVENTORY.product(), PRODUCT_CATALOG)
            .leftJoin(PRODUCT_CATALOG.std(), CLASS_DETAIL)
            .where(PRODUCT_CATALOG.active.eq(true).and(PRODUCT_CATALOG.std().isNull()).or(PRODUCT_CATALOG.std().session().id.eq(sessionId)))
            .fetch();
        return inventoryList;
    }
}
