package com.tulip.host.repository;

import com.tulip.host.domain.Session;
import com.tulip.host.domain.TransportCatalog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportCatalogRepository extends JpaRepository<TransportCatalog, Long> {
    List<TransportCatalog> findAllBySession(Session session);
}
