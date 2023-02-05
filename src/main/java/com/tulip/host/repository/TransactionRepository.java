package com.tulip.host.repository;

import com.tulip.host.data.TransactionReportDTO;
import com.tulip.host.domain.Transaction;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Double fetchTransactionTotal(Date from, Date to);

    List<TransactionReportDTO> fetchTransactionGroupBy(Date from, Date to);
}
