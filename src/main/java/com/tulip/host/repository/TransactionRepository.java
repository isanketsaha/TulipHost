package com.tulip.host.repository;

import com.tulip.host.data.TransactionReportDTO;
import com.tulip.host.domain.Transaction;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Double fetchTransactionTotal(Date from, Date to);

    List<TransactionReportDTO> fetchTransactionGroupBy(Date from, Date to, String groupByFormat);

    List<String> fetchAnnualFeesByClass(long studentId, long classId);

    List<Transaction> fetchStudentFeesTransactionByClassId(long studentId, long classId);

    List<Transaction> fetchAllTransactionByDues();

    Map<String, Map<String, Double>> fetchSalesReport(LocalDate date);
}
