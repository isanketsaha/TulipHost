package com.tulip.host.service;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.domain.QTransaction;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.Transaction;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.FeesLineItemRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransactionRepository;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.PayVM;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final StudentRepository studentRepository;

    private final SessionRepository sessionRepository;

    private final FeesLineItemRepository feesLineItemRepository;

    private final ApplicationProperties applicationProperties;

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
            CommonUtils.getPageRequest(DESC.toString(), "createdDate", pageNo, pageSize)
        );
        List<PaySummaryDTO> paySummaryDTOS = transactionMapper.toEntityList(transactionPage.getContent());
        return new PageImpl<PaySummaryDTO>(paySummaryDTOS, transactionPage.getPageable(), transactionPage.getTotalPages());
    }

    public List<PaySummaryDTO> fetchPaymentHistory(Long sessionId, Long studentId, int pageNo) {
        int size = applicationProperties.getPage().getSize();
        PageRequest pageRequest = PageRequest.of(((pageNo - 1) * size), pageNo * size);
        Session session = sessionRepository.findById(sessionId).orElse(null);
        Student student = studentRepository.findById(studentId).orElse(null);
        List<PaySummaryDTO> resultSet = new ArrayList<>();
        if (session != null && student != null) {
            //            Page<Transaction> purchaseOrders = transactionRepository.findAllByStudentAndCreatedDateBetween(student, from
            //                    .toInstant(), to.toInstant(), pageRequest);
            Collections.sort(resultSet, (item1, item2) -> item2.getPaymentDateTime().compareTo(item1.getPaymentDateTime()));
        }
        return resultSet;
    }
}
