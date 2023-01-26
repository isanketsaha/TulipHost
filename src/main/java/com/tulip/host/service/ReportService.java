package com.tulip.host.service;

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
import com.tulip.host.utils.CommonUtils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    public Page<PaySummaryDTO> fetchTransactionHistory(int pageNo, int pageSize) {
        Instant now = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        BooleanBuilder query = new BooleanBuilder().and(QTransaction.transaction.createdDate.goe(now));
        Page<Transaction> transactionPage = transactionPagedRepository.findAll(
            query,
            CommonUtils.getPageRequest(DESC, "createdDate", pageNo, pageSize)
        );
        List<PaySummaryDTO> toEntityList = transactionMapper.toEntityList(transactionPage.getContent());
        return new PageImpl<PaySummaryDTO>(toEntityList, transactionPage.getPageable(), transactionPage.getTotalElements());
    }

    public DashBoardStudentDTO studentReport() {
        Instant thisWeek = LocalDate.now().minus(7, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Instant thisMonth = LocalDate.now().minus(1, ChronoUnit.MONTHS).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BooleanBuilder weekCondition = new BooleanBuilder().and(QStudent.student.createdDate.goe(thisWeek));
        BooleanBuilder monthCondition = new BooleanBuilder().and(QStudent.student.createdDate.goe(thisMonth));

        return DashBoardStudentDTO
            .builder()
            .schoolStrength(studentRepository.fetchStudentCount(true, null))
            .studentAdmissionCountThisWeek(studentRepository.fetchStudentCount(true, weekCondition))
            .studentAdmissionCountThisMonth(studentRepository.fetchStudentCount(true, monthCondition))
            .build();
    }

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
        Collections.sort(inventoryItemDTOS, Comparator.comparing(InventoryItemDTO::getAvailableQty));
        return inventoryItemDTOS;
    }

    public double getTransactionTotal(Instant from, Instant to) {
        return transactionRepository.fetchTransactionTotal(from, to);
    }
}
