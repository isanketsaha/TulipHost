package com.tulip.host.repository;

import com.tulip.host.domain.Audit;
import com.tulip.host.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AuditRepository extends JpaRepository<Audit, Long>, QuerydslPredicateExecutor<Audit> {
    @Transactional
    @Modifying
    @Query("update Audit a set a.resolved = true where a.id = :id")
    int updateResolvedById(@Param("id") Long id);
}
