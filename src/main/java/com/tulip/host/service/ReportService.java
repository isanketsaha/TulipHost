package com.tulip.host.service;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.data.DashBoardStaffDTO;
import com.tulip.host.data.DashBoardStudentDTO;
import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.QStudent;
import com.tulip.host.domain.QTransaction;
import com.tulip.host.domain.Transaction;
import com.tulip.host.mapper.InventoryMapper;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransactionPagedRepository;
import com.tulip.host.repository.TransactionRepository;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    @org.springframework.transaction.annotation.Transactional
    public List<PaySummaryDTO> fetchTransactionHistory(Date date) {
        Date plus = DateUtils.addDays(date, 1);
        BooleanBuilder query = new BooleanBuilder()
            .and(QTransaction.transaction.createdDate.gt(date).and(QTransaction.transaction.createdDate.lt(plus)));
        Iterable<Transaction> transactions = transactionPagedRepository.findAll(query, Sort.by(DESC, "createdDate"));
        return transactionMapper.toEntityList(transactions);
    }

    @Transactional
    public DashBoardStudentDTO studentReport() {
        Date thisWeek = LocalDate.now().withDayOfWeek(MONDAY.getValue()).toDate();
        Date thisMonth = LocalDate.now().withDayOfMonth(1).toDate();
        BooleanBuilder admissionWeekCondition = new BooleanBuilder().and(QStudent.student.createdDate.goe(thisWeek));
        BooleanBuilder admissionMonthCondition = new BooleanBuilder().and(QStudent.student.createdDate.goe(thisMonth));
        BooleanBuilder withdrawnWeekCondition = new BooleanBuilder()
            .and(QStudent.student.terminationDate.isNotNull().and(QStudent.student.terminationDate.goe(thisWeek)));
        BooleanBuilder withdrawnMonthCondition = new BooleanBuilder()
            .and(QStudent.student.terminationDate.isNotNull().and(QStudent.student.terminationDate.goe(thisMonth)));

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
        List<Inventory> stockReport = inventoryRepository.stockReport();
        List<InventoryItemDTO> inventoryItemDTOS = inventoryMapper.toEntityList(stockReport);
        Collections.sort(inventoryItemDTOS, (a, b) -> a.getProduct().getItemName().compareTo(b.getProduct().getItemName()));
        return inventoryItemDTOS;
    }

    @Transactional
    public double getTransactionTotal(Date from, Date to) {
        return transactionRepository.fetchTransactionTotal(from, to);
    }
}
