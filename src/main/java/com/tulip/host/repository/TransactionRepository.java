package com.tulip.host.repository;

import com.tulip.host.domain.Transaction;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Double fetchTransactionTotal(Date from, Date to);

    Map<String, Long> fetchTransactionGroupBy(Date from, Date to);
}
