package com.tulip.host.repository;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.ProductCatalog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCatalogRepository extends JpaRepository<ProductCatalog, Long> {
    List<ProductCatalog> findAllByActiveAndStdOrStdIsNull(Boolean active, ClassDetail std);
}
