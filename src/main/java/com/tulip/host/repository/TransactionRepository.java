package com.tulip.host.repository;

import com.tulip.host.domain.Transaction;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, QuerydslPredicateExecutor<Transaction> {}
