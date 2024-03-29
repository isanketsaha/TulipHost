package com.tulip.host.service;

import com.tulip.host.data.TransactionReportDTO;
import com.tulip.host.data.TransactionSummary;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransactionRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitorService {

    private final TransactionRepository transactionRepository;

    private final StudentRepository studentRepository;

    private final TransactionMapper transactionMapper;

    public TransactionSummary transactionReport(LocalDate from, LocalDate to, String groupByFormat) {
        List<TransactionReportDTO> fetchTransactionGroupBy = transactionRepository.fetchTransactionGroupBy(from, to, groupByFormat);
        Collections.sort(fetchTransactionGroupBy, (a, b) -> a.getTransactionDate().compareTo(b.getTransactionDate()));
        TransactionSummary transactionSummary = TransactionSummary.builder().reportList(fetchTransactionGroupBy).build();
        fetchTransactionGroupBy.forEach(item -> {
            transactionSummary.setAmountTotal(transactionSummary.getAmountTotal() + item.getTotal());
            transactionSummary.setFeesTotal(transactionSummary.getFeesTotal() + item.getFees());
            transactionSummary.setExpenseTotal(transactionSummary.getExpenseTotal() + item.getExpense());
            transactionSummary.setPurchaseTotal(transactionSummary.getPurchaseTotal() + item.getPurchase());
        });
        return transactionSummary;
    }

    public void getProductList() {}
}
