package com.tulip.host.repository;

import com.tulip.host.domain.Audit;
import com.tulip.host.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AuditRepository extends JpaRepository<Audit, Long>, QuerydslPredicateExecutor<Audit> {}
