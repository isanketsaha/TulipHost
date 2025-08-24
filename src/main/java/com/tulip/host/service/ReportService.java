package com.tulip.host.service;

import static org.springframework.data.domain.Sort.Direction.DESC;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.data.DashBoardStaffDTO;
import com.tulip.host.data.DashBoardStudentDTO;
import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.QStudent;
import com.tulip.host.domain.QTransaction;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Transaction;
import com.tulip.host.mapper.InventoryMapper;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.StudentToTransportRepository;
import com.tulip.host.repository.TransactionPagedRepository;
import com.tulip.host.repository.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionPagedRepository transactionPagedRepository;

    private final TransactionRepository transactionRepository;

    private final StudentRepository studentRepository;

    private final TransactionMapper transactionMapper;

    private final EmployeeRepository employeeRepository;

    private final InventoryRepository inventoryRepository;

    private final InventoryMapper inventoryMapper;

    private final SessionService sessionService;
    private final StudentToTransportRepository studentToTransportRepository;

    @org.springframework.transaction.annotation.Transactional
    public List<PaySummaryDTO> fetchTransactionHistory(LocalDate date) {
        BooleanBuilder query = new BooleanBuilder()
            .and(
                QTransaction.transaction.createdDate
                    .gt(date.atStartOfDay())
                    .and(QTransaction.transaction.createdDate.lt(date.atTime(LocalTime.MAX)))
            );
        Iterable<Transaction> transactions = transactionPagedRepository.findAll(query, Sort.by(DESC, "createdDate"));
        return transactionMapper.toEntityList(transactions);
    }

    @Transactional
    public DashBoardStudentDTO studentReport() {
        BooleanBuilder admissionWeekCondition = new BooleanBuilder()
            .and(QStudent.student.createdDate.goe(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay()));
        BooleanBuilder admissionMonthCondition = new BooleanBuilder()
            .and(QStudent.student.createdDate.goe(LocalDate.now().withDayOfMonth(1).atStartOfDay()));
        BooleanBuilder withdrawnWeekCondition = new BooleanBuilder()
            .and(
                QStudent.student.terminationDate
                    .isNotNull()
                    .and(QStudent.student.terminationDate.goe(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay()))
            );
        BooleanBuilder withdrawnMonthCondition = new BooleanBuilder()
            .and(
                QStudent.student.terminationDate
                    .isNotNull()
                    .and(QStudent.student.terminationDate.goe(LocalDate.now().withDayOfMonth(1).atStartOfDay()))
            );

        return DashBoardStudentDTO
            .builder()
            .schoolStrength(studentRepository.fetchStudentCount(true, null))
            .admissionThisWeek(studentRepository.fetchStudentCount(true, admissionWeekCondition))
            .admissionThisMonth(studentRepository.fetchStudentCount(true, admissionMonthCondition))
            .withdrawnThisWeek(studentRepository.fetchStudentCount(false, withdrawnWeekCondition))
            .withdrawnThisMonth(studentRepository.fetchStudentCount(false, withdrawnMonthCondition))
            .build();
    }

    @Transactional
    public DashBoardStaffDTO staffReport() {
        Map<String, Long> fetchStaffReport = employeeRepository.fetchStaffReport();

        return DashBoardStaffDTO
            .builder()
            .staffTypeCount(fetchStaffReport)
            .staffCount(fetchStaffReport.entrySet().stream().mapToLong(x -> x.getValue()).sum())
            .build();
    }

    @Transactional
    public List<InventoryItemDTO> inventoryReport() {
        List<Inventory> stockReport = inventoryRepository.stockReportWithIndex();
        List<InventoryItemDTO> inventoryItemDTOS = inventoryMapper.toEntityList(stockReport);

        if (inventoryItemDTOS.size() > 100) {
            inventoryItemDTOS.parallelStream()
                    .sorted((a, b) -> a.getProduct().getItemName().compareTo(b.getProduct().getItemName()))
                    .collect(Collectors.toList());
        } else {
            Collections.sort(inventoryItemDTOS,
                    (a, b) -> a.getProduct().getItemName().compareTo(b.getProduct().getItemName()));
        }

        return inventoryItemDTOS;
    }

    @Transactional
    public double getTransactionTotal(LocalDate from, LocalDate to) {
        return transactionRepository.fetchTransactionTotal(from, to);
    }

    public Map<String, Integer> transportReport() {
        Session session = sessionService.currentSession();
        Map<String, List<Long>> report = studentToTransportRepository.findReport(session);
        return report.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, item -> item.getValue().size()));
    }

    public Map<String, Map<String, Double>> salesReport(java.time.LocalDate date) {
        Map<String, Map<String, Double>> stringMapMap = transactionRepository.fetchSalesReport(date);
        return stringMapMap;
    }
}
