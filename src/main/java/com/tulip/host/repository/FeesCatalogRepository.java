package com.tulip.host.repository;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.FeesCatalog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeesCatalogRepository extends JpaRepository<FeesCatalog, Long> {
    List<FeesCatalog> findAllByActiveAndStd(Boolean active, ClassDetail std);
}
