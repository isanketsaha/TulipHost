package com.tulip.host.repository;

import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.PurchaseCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseCatalogRepository extends JpaRepository<PurchaseCatalog, Long> {}
