package com.tulip.host.repository;

import com.tulip.host.data.TransactionReportDTO;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Transaction;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Double fetchTransactionTotal(LocalDate from, LocalDate to);

    List<TransactionReportDTO> fetchTransactionGroupBy(LocalDate from, LocalDate to, String groupByFormat);

    List<String> fetchAnnualFeesByClass(Long studentId, Long classId);

    List<Transaction> fetchStudentFeesTransactionByClassId(long studentId, long classId);

    List<Transaction> fetchAllTransactionByDues();

    List<Transaction> fetchAllTransactionByDuesWithLimit(int limit);

    Map<String, Map<String, Double>> fetchSalesReport(LocalDate date);

    List<String> fetchTransportMonths(Long studentId, Session sessionId);

    List<Transaction> checkIfTransportPaid(Long studentId, Long transportId, String month);

    List<Object[]> fetchPendingFeesBatch(List<Long> studentIds, Long classId, Long sessionId);

    Map<Long, List<String>> fetchAnnualFeesByClassBatch(List<Long> studentIds, Long classId);
}
