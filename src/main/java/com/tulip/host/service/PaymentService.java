package com.tulip.host.service;

import static com.tulip.host.config.Constants.MONTH_YEAR_FORMAT;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.data.FeesGraphDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.domain.*;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.enums.PaymentOptionEnum;
import com.tulip.host.mapper.ExpenseMapper;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.ExpenseRepository;
import com.tulip.host.repository.FeesLineItemRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransactionPagedRepository;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.ExpenseItemVM;
import com.tulip.host.web.rest.vm.PayVM;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionPagedRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final StudentRepository studentRepository;

    private final SessionRepository sessionRepository;

    private final FeesLineItemRepository feesLineItemRepository;

    private final ApplicationProperties applicationProperties;

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT, Locale.ENGLISH);

    @Transactional
    public Long payFees(PayVM payVM) {
        Transaction transaction = transactionMapper.toModel(payVM);
        transaction
            .getFeesLineItem()
            .forEach(item -> {
                item.setOrder(transaction);
            });
        Transaction save = transactionRepository.save(transaction);
        return save.getId();
    }

    @Transactional
    public Long payPurchase(PayVM payVM) {
        Transaction transaction = transactionMapper.toModel(payVM);
        transaction
            .getPurchaseLineItems()
            .forEach(item -> {
                item.setOrder(transaction);
            });
        Transaction purchaseOrder = transactionRepository.save(transaction);
        return purchaseOrder.getId();
    }

    public PaySummaryDTO paymentDetails(Long paymentId) {
        Transaction feesOrder = transactionRepository.findById(paymentId).orElse(null);
        if (feesOrder != null) {
            return transactionMapper.toEntity(feesOrder);
        }
        return null;
    }

    public PageImpl<PaySummaryDTO> getTransactionHistory(int pageNo, Long studentId, int pageSize) {
        BooleanBuilder booleanBuilder = new BooleanBuilder().and(QTransaction.transaction.student().id.eq(studentId));
        Page<Transaction> transactionPage = transactionRepository.findAll(
            booleanBuilder.getValue(),
            CommonUtils.getPageRequest(DESC, "createdDate", pageNo, pageSize)
        );
        List<PaySummaryDTO> paySummaryDTOS = transactionMapper.toEntityList(transactionPage.getContent());
        return new PageImpl<PaySummaryDTO>(paySummaryDTOS, transactionPage.getPageable(), transactionPage.getTotalElements());
    }

    public FeesGraphDTO getFeesGraph(Long studentId, Long classId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder()
            .and(
                QTransaction.transaction
                    .student()
                    .id.eq(studentId)
                    .and(QTransaction.transaction.type.eq(PayTypeEnum.FEES.name()))
                    .and(
                        QTransaction.transaction.feesLineItem
                            .any()
                            .feesProduct()
                            .feesName.equalsIgnoreCase("Tution Fees")
                            .and(QTransaction.transaction.feesLineItem.any().feesProduct().std().id.eq(classId))
                    )
            );

        List<Transaction> transactionList = (List<Transaction>) transactionRepository.findAll(booleanBuilder, Sort.by(DESC, "createdDate"));
        if (CollectionUtils.isNotEmpty(transactionList)) {
            Set<String> months = new LinkedHashSet<>();
            Student student = null;
            for (Transaction transaction : transactionList) {
                student = transaction.getStudent();
                transaction
                    .getFeesLineItem()
                    .stream()
                    .filter(item -> item.getFeesProduct().getFeesName().equalsIgnoreCase("Tution Fees"))
                    .forEach(item -> {
                        months.addAll(findMonthsBetweenDates(item.getFromMonth(), item.getToMonth()));
                    });
            }
            return FeesGraphDTO.builder().admissionDate(student.getCreatedDate()).paidMonths(months).build();
        }
        return null;
    }

    private List<String> findMonthsBetweenDates(String from, String to) {
        List<String> allMonths = new ArrayList<>();
        YearMonth startDate = YearMonth.parse(from, formatter);
        YearMonth endDate = YearMonth.parse(to, formatter);
        while (startDate.isBefore(endDate)) {
            allMonths.add(startDate.format(formatter).split("/")[0]);
            startDate = startDate.plusMonths(1);
        }
        allMonths.add(endDate.format(formatter).split("/")[0]);
        return allMonths;
    }

    public Long registerExpense(List<ExpenseItemVM> expenseItems) {
        Set<Expense> expenses = expenseMapper.toModelList(expenseItems);
        Transaction transaction = Transaction
            .builder()
            .expenseItems(expenses)
            .paymentMode(PaymentOptionEnum.CASH.name())
            .amount(expenses.stream().mapToDouble(item -> item.getAmount()).sum() * (-1))
            .type(PayTypeEnum.EXPENSE.name())
            .build();
        transaction.setAfterDiscount(transaction.getAmount());
        expenses.stream().forEach(item -> item.setOrder(transaction));
        List<Expense> expensesList = expenseRepository.saveAllAndFlush(expenses);
        return expensesList.stream().findFirst().get().getOrder().getId();
    }
}
