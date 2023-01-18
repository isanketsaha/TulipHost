package com.tulip.host.repository.impl;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.ProductCatalogRepository;
import java.util.List;
import javax.persistence.EntityManager;

public class ProductCatalogRepositoryImpl extends BaseRepositoryImpl<ProductCatalog, Long> implements ProductCatalogRepository {

    protected ProductCatalogRepositoryImpl(EntityManager em) {
        super(ProductCatalog.class, em);
    }

    @Override
    public List<ProductCatalog> findAllByActiveProduct(Long classId) {
        return jpaQueryFactory
            .selectFrom(PRODUCT_CATALOG)
            .where(PRODUCT_CATALOG.active.eq(true).and(PRODUCT_CATALOG.std().isNull()).or(PRODUCT_CATALOG.std().id.eq(classId)))
            .fetch();
    }
}
