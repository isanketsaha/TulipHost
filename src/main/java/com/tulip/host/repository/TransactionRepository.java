package com.tulip.host.repository;

import com.tulip.host.domain.Transaction;
import java.time.Instant;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Double fetchTransactionTotal(Instant from, Instant to);

    Map<String, Long> fetchTransactionGroupBy(Instant from, Instant to);
}
