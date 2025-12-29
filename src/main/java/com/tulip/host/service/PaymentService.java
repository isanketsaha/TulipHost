package com.tulip.host.service;

import static com.tulip.host.config.Constants.DUES;
import static com.tulip.host.config.Constants.MONTH_YEAR_FORMAT;
import static com.tulip.host.config.Constants.TRANSPORT_FEES;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.service.communication.CommunicationRequest;
import com.tulip.host.service.communication.OutboundCommunicationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.data.FeesGraphDTO;
import com.tulip.host.data.FeesItemSummaryDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Dues;
import com.tulip.host.domain.DuesPayment;
import com.tulip.host.domain.Expense;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.domain.PurchaseLineItem;
import com.tulip.host.domain.QTransaction;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.Transaction;
import com.tulip.host.domain.TransportCatalog;
import com.tulip.host.domain.Upload;
import com.tulip.host.enums.DueStatusEnum;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.mapper.DuesMapper;
import com.tulip.host.mapper.DuesPaymentMapper;
import com.tulip.host.mapper.ExpenseMapper;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.mapper.UploadMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.DuesPaymentRepository;
import com.tulip.host.repository.DuesRepository;
import com.tulip.host.repository.FeesCatalogRepository;
import com.tulip.host.repository.FeesLineItemRepository;
import com.tulip.host.repository.ProductCatalogRepository;
import com.tulip.host.repository.PurchaseLineItemRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransactionPagedRepository;
import com.tulip.host.repository.TransactionRepository;
import com.tulip.host.repository.TransportCatalogRepository;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.DuePaymentVm;
import com.tulip.host.web.rest.vm.DueVM;
import com.tulip.host.web.rest.vm.EditOrderVm;
import com.tulip.host.web.rest.vm.ExpenseVm;
import com.tulip.host.web.rest.vm.FileUploadVM;
import com.tulip.host.web.rest.vm.PayVM;

import jakarta.transaction.Transactional;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransportCatalogRepository transportCatalogRepository;

    private final DuesPaymentRepository duesPaymentRepository;
    private final DuesRepository duesRepository;

    private final PurchaseLineItemRepository purchaseLineItemRepository;
    private final TransactionRepository transactionRepository;

    private final ProductCatalogRepository productCatalogRepository;
    private final FeesCatalogRepository feesCatalogRepository;

    private final TransactionPagedRepository transactionPagedRepository;

    private final TransactionMapper transactionMapper;

    private final StudentRepository studentRepository;

    private final FeesLineItemRepository feesLineItemRepository;


    private final CouponService couponService;

    private final ExpenseMapper expenseMapper;


    private final DuesMapper duesMapper;

    private final DuesPaymentMapper duesPaymentMapper;

    private final UploadMapper uploadMapper;

    private final ClassDetailRepository classDetailRepository;

    private final InventoryAllocationService inventoryAllocationService;

    private static final String TUITION_FEES = "TUITION";

    private final OutboundCommunicationService outboundCommunicationService;

    private final MailService mailService;

    private final UrlShortener urlShortener;

    private final UploadService uploadService;

    /**
     * Allocates inventory to purchase line items using smart allocation strategy
     * This method handles price differences and automatically selects optimal
     * inventory batches
     */
    private void allocateInventoryToPurchaseItems(Set<PurchaseLineItem> purchaseLineItems) {
        for (PurchaseLineItem lineItem : purchaseLineItems) {
            // Validate price first
            // inventoryAllocationService.validatePurchasePrice(lineItem);

            // Use the smart inventory allocation service
            inventoryAllocationService.allocateInventoryToPurchaseItem(lineItem);
        }
    }

    @Transactional
    public Long payFees(PayVM payVM) throws ValidationException {
        validate(payVM);
        Transaction transaction = transactionMapper.toModel(payVM);
        transaction
            .getFeesLineItem()
            .forEach(item -> {
                item.setOrder(transaction);
            });
        if (payVM.isDueOpted()) {
            applyDues(transaction, payVM.getDueInfo());
        }

        if (!StringUtils.isEmpty(payVM.getCouponCode())) {
            couponService.applyCoupon(transaction, payVM);
        }

        Transaction save = transactionRepository.save(transaction);
        return save.getId();
    }

    private void applyDues(Transaction transaction, DueVM dueInfo) {
        Dues dues = duesMapper.toEntity(dueInfo);
        transaction.setDues(dues);
        if (dueInfo.getDuesDocs() != null) {
            Upload upload = uploadMapper.toModel(dueInfo.getDuesDocs());
            upload.setDocumentType(DUES);
            applyUpload(dueInfo.getDuesDocs(), transaction);
            transaction.addToUploadList(upload);
        }
        dues.setTransaction(transaction);
    }

    private void applyUpload(FileUploadVM docs, Transaction transaction) {
        if (docs != null) {
            Upload upload = uploadMapper.toModel(docs);
            upload.setDocumentType(DUES);
            upload.setTransactionDocs(transaction);
            transaction.addToUploadList(upload);
        }
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
                        ProductCatalog productCatalog = productCatalogRepository.findById(item.getProductId())
                            .orElse(null);
                        double expectedPrice = inventoryAllocationService.calculateExpectedPrice(productCatalog);
                        if (Math.abs(expectedPrice - item.getUnitPrice()) > 0.01
                            || Math.abs(amount - item.getAmount()) > 0.01) {
                            errors.add("Incorrect LineItem Amount");
                        }
                        return item;
                    })
                    .mapToDouble(lineItem -> lineItem.getAmount())
                    .sum();

                if (sum != payVM.getTotal() + (payVM.isDueOpted() ? payVM.getDueInfo()
                    .getDueAmount() : 0)
                    || sum != payVM.getSubTotal()) errors.add("Incorrect Total");
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
                        if (item.getType()
                            .equals("CLASS_FEES")) {
                            FeesCatalog feesCatalog = feesCatalogRepository.findById(item.getFeesId())
                                .orElse(null);
                            Student student = studentRepository.checkIfFeesPaid(payVM.getStudentId(), item.getFeesId(), item.getMonth());
                            if (feesCatalog.getPrice() != item.getUnitPrice()) {
                                errors.add("Incorrect Fees Price ");
                            }
                            if (student != null) {
                                String monthString = feesCatalog.getApplicableRule()
                                    .equals(FeesRuleType.MONTHLY)
                                    ? "for month -  " + item.getMonth()
                                    : "";
                                errors.add(feesCatalog.getFeesName() + " is already paid " + monthString);
                            }
                        } else if (item.getType()
                            .equals(TRANSPORT_FEES.replace(" ", "_"))) {
                            TransportCatalog transportCatalog = transportCatalogRepository.findById(item.getFeesId())
                                .orElseThrow();
                            if (transportCatalog.getAmount() != item.getUnitPrice()) {
                                errors.add("Incorrect Fees Price ");
                            }
                            List<Transaction> feesLineItems = transactionRepository.checkIfTransportPaid(
                                payVM.getStudentId(),
                                item.getFeesId(),
                                item.getMonth()
                            );
                            if (CollectionUtils.isNotEmpty(feesLineItems)) {
                                errors.add("Transport Fees" + " is already paid " + "for month -  " + item.getMonth());
                            }
                        }
                        return item;
                    })
                    .mapToDouble(lineItem -> lineItem.getAmount())
                    .sum();
                if (sum != payVM.getTotal() + (payVM.isDueOpted() ? payVM.getDueInfo()
                    .getDueAmount() : 0)
                    + (StringUtils.isNotEmpty(payVM.getCouponCode()) ? payVM.getDiscountAmount() : 0) || sum != payVM.getSubTotal())
                    errors.add("Incorrect  Total");
            }
        }
        if (!CollectionUtils.isEmpty(errors)) {
            throw new ValidationException(errors.toString(), payVM.getClass()
                .getName());
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
        if (payVM.isDueOpted()) {
            applyDues(transaction, payVM.getDueInfo());
        }
        allocateInventoryToPurchaseItems(transaction.getPurchaseLineItems());
        Transaction purchaseOrder = transactionRepository.save(transaction);
        return purchaseOrder.getId();
    }

    public Transaction getTransactionRecord(Long paymentId) {
        return transactionRepository.findById(paymentId)
            .orElse(null);
    }

    @Transactional
    public List<PaySummaryDTO> getTransactionRecordByDate(LocalDate localDate, String condition) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        if (condition.equalsIgnoreCase("MONTH")) {
            LocalDate startDate = localDate.withDayOfMonth(1);
            LocalDate endDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
            startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
            endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        } else if (condition.equalsIgnoreCase("YEAR")) {
            LocalDate startDate = localDate.withDayOfYear(1);
            LocalDate endDate = localDate.withDayOfYear(localDate.lengthOfYear());
            startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
            endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        } else {
            startDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
            endDateTime = LocalDateTime.of(localDate, LocalTime.MAX);
        }

        BooleanBuilder query = new BooleanBuilder().and(QTransaction.transaction.createdDate.between(startDateTime, endDateTime));

        Iterable<Transaction> transactions = transactionPagedRepository.findAll(query, Sort.by(Sort.Direction.DESC, "createdDate"));
        return transactionMapper.toEntityList(transactions);
    }

    @Transactional
    public PaySummaryDTO paymentDetails(Long paymentId) {
        Transaction feesOrder = getTransactionRecord(paymentId);
        if (feesOrder != null) {
            PaySummaryDTO paySummaryDTO = transactionMapper.toEntity(feesOrder);
            Collections.sort(
                paySummaryDTO.getFeesItem(),
                Comparator
                    .comparing(FeesItemSummaryDTO::getFeesTitle)
                    .thenComparing((o1, o2) -> {
                        try {
                            SimpleDateFormat fmt = new SimpleDateFormat(MONTH_YEAR_FORMAT, Locale.US);
                            return fmt.parse(o1.getMonth())
                                .compareTo(fmt.parse(o2.getMonth()));
                        } catch (ParseException ex) {
                            return o1.getMonth()
                                .compareTo(o2.getMonth());
                        }
                    })
            );
            return paySummaryDTO;
        }
        return null;
    }

    @Transactional
    public PageImpl<PaySummaryDTO> getTransactionHistory(int pageNo, Long studentId, int pageSize) {
        BooleanBuilder booleanBuilder = new BooleanBuilder()
            .and(QTransaction.transaction.student().id.eq(studentId));

        Page<Transaction> transactionPage = transactionPagedRepository.findAll(
            booleanBuilder.getValue(),
            CommonUtils.getPageRequest(DESC, "createdDate", pageNo, pageSize)
        );

        List<PaySummaryDTO> paySummaryDTOS = transactionMapper.toEntityList(transactionPage.getContent());

        return new PageImpl<PaySummaryDTO>(
            paySummaryDTOS,
            transactionPage.getPageable(),
            transactionPage.getTotalElements());

    }

    @Transactional
    public FeesGraphDTO getFeesGraph(Long studentId, Long classId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder()
            .and(
                QTransaction.transaction
                    .student()
                    .id.eq(studentId)
                    .and(QTransaction.transaction.type.eq(PayTypeEnum.FEES.name()))
                    .and(QTransaction.transaction.feesLineItem.any()
                        .feesProduct()
                        .std().id.eq(classId))
            );

        List<Transaction> transactionList = (List<Transaction>) transactionPagedRepository.findAll(
            booleanBuilder,
            Sort.by(DESC, "createdDate")
        );
        ClassDetail byClass = classDetailRepository.findByClass(classId);
        List<String> transportSummary = transactionRepository.fetchTransportMonths(studentId, byClass.getSession());
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
                        TransportCatalog transportCatalog = item.getTransport();
                        if (feesProduct != null) {
                            if (
                                feesProduct.getApplicableRule()
                                    .equals(FeesRuleType.MONTHLY) &&
                                    feesProduct.getFeesName()
                                        .startsWith(TUITION_FEES)
                            ) {
                                months.add(item.getMonth());
                            } else if (feesProduct.getApplicableRule()
                                .equals(FeesRuleType.YEARLY)) {
                                annual.add(feesProduct.getId());
                            }
                        }
                    });
            }

            return FeesGraphDTO
                .builder()
                .admissionDate(student.getCreatedDate())
                .paidMonths(months)
                .annualFeesPaid(annual)
                .transportMonths(new HashSet<>(transportSummary))
                .build();
        }
        return null;
    }

    @Transactional
    public Long registerExpense(ExpenseVm expenseItems) {
        Set<Expense> expenses = expenseMapper.toModelList(expenseItems.getExpenseItem());
        if (expenses.stream()
            .mapToDouble(Expense::getAmount)
            .sum() == expenseItems.getTotal()) {
            Transaction transaction = Transaction
                .builder()
                .expenseItems(expenses)
                .paymentMode(expenseItems.getPaymentMode())
                .amount(expenses.stream()
                    .mapToDouble(Expense::getAmount)
                    .sum() * (-1))
                .type(PayTypeEnum.EXPENSE.name())
                .build();
            expenses.forEach(item -> item.setReceivedBy(expenseItems.getReceivedBy()
                .toUpperCase()));
            if (CollectionUtils.isNotEmpty(expenseItems.getExpenseDocs())) {
                Set<Upload> uploadSet = uploadMapper.toModelList(expenseItems.getExpenseDocs());
                transaction.setUploadList(uploadSet);
                uploadSet.forEach(upload -> {
                    upload.setTransactionDocs(transaction);
                    upload.setDocumentType("EXPENSE");
                });
            }
            transaction.setComments(expenseItems.getComments());
            transaction.setAfterDiscount(transaction.getAmount());
            expenses.forEach(item -> item.setOrder(transaction));
            transactionRepository.saveAndFlush(transaction);
            return transaction.getId();
        } else {
            throw new RuntimeException("Error in calculating amount");
        }
    }

    @Transactional
    public void edit(EditOrderVm editOrderVm) {
        Transaction transaction = transactionRepository.findById(editOrderVm.getPaymentId())
            .orElse(null);
        double sum = 0;
        if (editOrderVm.getPayTypeEnum() == PayTypeEnum.FEES) {
            FeesLineItem feesLineItem = feesLineItemRepository.findById(editOrderVm.getItemId())
                .orElse(null);
            assert transaction != null;
            transaction.removeFeesLineItem(feesLineItem);
            if (!transaction.getFeesLineItem()
                .isEmpty()) {
                sum = transaction.getFeesLineItem()
                    .stream()
                    .mapToDouble(FeesLineItem::getAmount)
                    .sum();
                transaction.setAmount(sum);
                transactionRepository.saveAndFlush(transaction);
            } else {
                transactionRepository.delete(transaction);
            }
            assert feesLineItem != null;
            feesLineItemRepository.delete(feesLineItem);
        } else if (editOrderVm.getPayTypeEnum() == PayTypeEnum.PURCHASE) {
            PurchaseLineItem purchaseLineItem = purchaseLineItemRepository.findById(editOrderVm.getItemId())
                .orElse(null);
            assert transaction != null;
            transaction.removePurchaseLineItems(purchaseLineItem);
            if (!transaction.getPurchaseLineItems()
                .isEmpty()) {
                sum = transaction.getPurchaseLineItems()
                    .stream()
                    .mapToDouble(PurchaseLineItem::getAmount)
                    .sum();
                transaction.setAmount(sum);
                transactionRepository.saveAndFlush(transaction);
            } else {
                transactionRepository.delete(transaction);
            }
            assert purchaseLineItem != null;
            purchaseLineItemRepository.delete(purchaseLineItem);
        }
    }

    public void deleteTransaction(long transactionId) {
        transactionRepository.delete(transactionRepository.findById(transactionId)
            .orElse(null));
    }

    @Transactional
    public List<PaySummaryDTO> allDues() {
        List<Transaction> transactionPage = transactionRepository.fetchAllTransactionByDuesWithLimit(1000);
        return transactionMapper.toEntityList(transactionPage);
    }

    @Transactional
    public long payDues(DuePaymentVm duePaymentVm) throws ValidationException {
        Dues dues = duesRepository.findById(duePaymentVm.getDueId())
            .orElse(null);
        if (dues != null && !dues.getStatus()
            .equals(DueStatusEnum.PAID)) {
            final Double paidAmount = dues.getDuesPayment()
                .stream()
                .map(item -> item.getAmount())
                .reduce(0.0, Double::sum);
            if (paidAmount + duePaymentVm.getAmount() <= dues.getDueAmount()) {
                DuesPayment duesPayment = duesPaymentMapper.toEntity(duePaymentVm);
                dues.addDuesPayment(duesPayment);
                duesPayment.setDue(dues);
                if (paidAmount + duePaymentVm.getAmount() == dues.getDueAmount()) {
                    dues.setStatus(DueStatusEnum.PAID.name());
                }
                DuesPayment payment = duesPaymentRepository.saveAndFlush(duesPayment);
                return payment.getId();
            } else {
                throw new ValidationException("Paid amount is greater than due amount or its paid");
            }
        }
        return -1;
    }

    @Transactional
    public void attachInvoice(Long paymentId, FileUploadVM save) {
        Transaction transaction = transactionRepository.findById(paymentId)
            .orElseThrow();
        Upload upload = uploadMapper.toModel(save);
        transaction.setInvoice(upload);
        transactionRepository.saveAndFlush(transaction);
        notifyTransaction(transaction);
    }

    private void notifyTransaction(Transaction transaction) {
        if (List.of(PayTypeEnum.PURCHASE.name(), PayTypeEnum.FEES.name())
            .contains(transaction.getType())) {
            String url = urlShortener.shortenUrl(uploadService.getURL(transaction.getInvoice()
                    .getUid()),
                "TRANSACTION_SLIP");
            Map<String, Object> map = Map.of("studentName", transaction.getStudent()
                    .getName(),
                "amount", transaction.getAfterDiscount(), "receiptNo", transaction.getId(),
                "paymentType", transaction.getType(), "shortUrl", url,
                "paymentMode", transaction.getPaymentMode());
            outboundCommunicationService.send(CommunicationRequest.builder()
                .channel(CommunicationChannel.SMS)
                .recipient(new String[]{transaction.getStudent().getPhoneNumber()})
                .content(mailService.renderTemplate("mail/payment_successful.vm", map))
                .entityType("PAYMENT")
                .build());
        }
    }
}
