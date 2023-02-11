package com.tulip.host.service;

import static com.tulip.host.config.Constants.MONTH_YEAR_FORMAT;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.data.FeesGraphDTO;
import com.tulip.host.data.FeesItemSummaryDTO;
import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.domain.*;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.enums.PaymentOptionEnum;
import com.tulip.host.mapper.ExpenseMapper;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.ExpenseRepository;
import com.tulip.host.repository.FeesCatalogRepository;
import com.tulip.host.repository.FeesLineItemRepository;
import com.tulip.host.repository.ProductCatalogRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransactionPagedRepository;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.errors.BadRequestAlertException;
import com.tulip.host.web.rest.vm.ExpenseItemVM;
import com.tulip.host.web.rest.vm.PayVM;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.transaction.Transactional;
import javax.xml.bind.ValidationException;
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

    private final ProductCatalogRepository productCatalogRepository;
    private final FeesCatalogRepository feesCatalogRepository;

    private final TransactionPagedRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final StudentRepository studentRepository;

    private final SessionRepository sessionRepository;

    private final FeesLineItemRepository feesLineItemRepository;

    private final ApplicationProperties applicationProperties;

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    private final SessionService sessionService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT, Locale.ENGLISH);

    @Transactional
    public Long payFees(PayVM payVM) throws ValidationException {
        validate(payVM);
        Transaction transaction = transactionMapper.toModel(payVM);
        transaction
            .getFeesLineItem()
            .forEach(item -> {
                item.setOrder(transaction);
            });
        Transaction save = transactionRepository.save(transaction);
        return save.getId();
    }

    private void validate(PayVM payVM) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (payVM.getPayType() == PayTypeEnum.PURCHASE) {
            if (CollectionUtils.isEmpty(payVM.getPurchaseItems())) {
                errors.add("Incorrect Purchase Items");
            } else {
                double sum = payVM
                    .getPurchaseItems()
                    .stream()
                    .map(item -> {
                        double amount = item.getQty() * item.getUnitPrice();
                        ProductCatalog productCatalog = productCatalogRepository.findById(item.getProductId()).orElse(null);
                        if (productCatalog.getPrice() != item.getUnitPrice() || amount != item.getAmount()) {
                            errors.add("Incorrect LineItem Amount");
                        }
                        return item;
                    })
                    .mapToDouble(lineItem -> lineItem.getAmount())
                    .sum();

                if (sum != payVM.getTotal()) errors.add("Incorrect Total");
            }
        }
        if (payVM.getPayType() == PayTypeEnum.FEES) {
            if (CollectionUtils.isEmpty(payVM.getFeeItem())) {
                errors.add("Incorrect Fees Item");
            } else {
                double sum = payVM
                    .getFeeItem()
                    .stream()
                    .map(item -> {
                        FeesCatalog feesCatalog = feesCatalogRepository.findById(item.getFeesId()).orElse(null);
                        Student student = studentRepository.checkIfFeesPaid(payVM.getStudentId(), item.getFeesId(), item.getMonth());
                        if (feesCatalog.getPrice() != item.getUnitPrice()) {
                            errors.add("Incorrect Fees Price ");
                        }
                        if (student != null) {
                            errors.add(feesCatalog.getFeesName() + " already paid for month -  " + item.getMonth());
                        }
                        return item;
                    })
                    .mapToDouble(lineItem -> lineItem.getAmount())
                    .sum();
                if (sum != payVM.getTotal()) errors.add("Incorrect  Total");
            }
        }
        if (!CollectionUtils.isEmpty(errors)) {
            throw new ValidationException(errors.toString(), payVM.getClass().getName());
        }
    }

    @Transactional
    public Long payPurchase(PayVM payVM) throws ValidationException {
        validate(payVM);
        Transaction transaction = transactionMapper.toModel(payVM);
        transaction
            .getPurchaseLineItems()
            .forEach(item -> {
                item.setOrder(transaction);
            });
        Transaction purchaseOrder = transactionRepository.save(transaction);
        return purchaseOrder.getId();
    }

    @Transactional
    public PaySummaryDTO paymentDetails(Long paymentId) {
        Transaction feesOrder = transactionRepository.findById(paymentId).orElse(null);
        if (feesOrder != null) {
            PaySummaryDTO paySummaryDTO = transactionMapper.toEntity(feesOrder);
            Collections.sort(
                paySummaryDTO.getFeesItem(),
                Comparator
                    .comparing(FeesItemSummaryDTO::getFeesTitle)
                    .thenComparing((o1, o2) -> {
                        try {
                            SimpleDateFormat fmt = new SimpleDateFormat("MMM/YYYY", Locale.US);
                            return fmt.parse(o1.getMonth()).compareTo(fmt.parse(o2.getMonth()));
                        } catch (ParseException ex) {
                            return o1.getMonth().compareTo(o2.getMonth());
                        }
                    })
            );
            return paySummaryDTO;
        }
        return null;
    }

    @Transactional
    public PageImpl<PaySummaryDTO> getTransactionHistory(int pageNo, Long studentId, int pageSize) {
        BooleanBuilder booleanBuilder = new BooleanBuilder().and(QTransaction.transaction.student().id.eq(studentId));
        Page<Transaction> transactionPage = transactionRepository.findAll(
            booleanBuilder.getValue(),
            CommonUtils.getPageRequest(DESC, "createdDate", pageNo, pageSize)
        );
        List<PaySummaryDTO> paySummaryDTOS = transactionMapper.toEntityList(transactionPage.getContent());
        return new PageImpl<PaySummaryDTO>(paySummaryDTOS, transactionPage.getPageable(), transactionPage.getTotalElements());
    }

    @Transactional
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
                            .feesName.startsWithIgnoreCase("Tuition")
                            .and(QTransaction.transaction.feesLineItem.any().feesProduct().std().id.eq(classId))
                    )
            );

        List<Transaction> transactionList = (List<Transaction>) transactionRepository.findAll(booleanBuilder, Sort.by(DESC, "createdDate"));
        if (CollectionUtils.isNotEmpty(transactionList)) {
            Set<String> months = new LinkedHashSet<>();
            Set<Long> annual = new LinkedHashSet<>();
            Student student = null;
            for (Transaction transaction : transactionList) {
                student = transaction.getStudent();
                transaction
                    .getFeesLineItem()
                    .stream()
                    .forEach(item -> {
                        FeesCatalog feesProduct = item.getFeesProduct();
                        if (
                            feesProduct.getApplicableRule().equals(FeesRuleType.MONTHLY) && feesProduct.getFeesName().startsWith("Tuition")
                        ) {
                            months.add(item.getMonth());
                        } else if (feesProduct.getApplicableRule().equals(FeesRuleType.YEARLY)) {
                            annual.add(feesProduct.getId());
                        }
                    });
            }
            return FeesGraphDTO.builder().admissionDate(student.getCreatedDate()).paidMonths(months).annualFeesPaid(annual).build();
        }
        return null;
    }

    @Transactional
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
