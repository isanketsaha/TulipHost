package com.tulip.host.repository.impl;

import static com.tulip.host.config.Constants.CATEGORY_BOOK;
import static com.tulip.host.config.Constants.CATEGORY_PLACEHOLDER;

import com.tulip.host.domain.Inventory;
import com.tulip.host.repository.InventoryRepository;
import java.util.List;
import javax.persistence.EntityManager;

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
                    .and(PRODUCT_CATALOG.category.ne(CATEGORY_PLACEHOLDER).and(PRODUCT_CATALOG.category.ne(CATEGORY_BOOK)))
            )
            .fetch();
    }
}
