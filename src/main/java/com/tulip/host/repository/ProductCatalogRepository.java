package com.tulip.host.repository;

import com.tulip.host.domain.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCatalogRepository extends JpaRepository<ProductCatalog, Long> {}
