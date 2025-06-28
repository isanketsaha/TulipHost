package com.tulip.host.repository;

import com.tulip.host.domain.SystemDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentRepository extends JpaRepository<SystemDocument, Long>, JpaSpecificationExecutor<SystemDocument> {
}
