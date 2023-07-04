package com.tulip.host.repository;

import com.tulip.host.domain.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductCatalogPagedRepository extends JpaRepository<ProductCatalog, Long> {
    @Transactional
    @Modifying
    @Query("update ProductCatalog a set a.active = :active where a.id = :id")
    void updateProductVisibility(@Param("id") long id, @Param("active") boolean active);
}
