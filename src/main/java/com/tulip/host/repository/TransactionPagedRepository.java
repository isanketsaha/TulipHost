package com.tulip.host.repository;

import com.tulip.host.domain.Transaction;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TransactionPagedRepository extends JpaRepository<Transaction, Long>, QuerydslPredicateExecutor<Transaction> {}
