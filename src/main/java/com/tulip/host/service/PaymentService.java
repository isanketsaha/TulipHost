package com.tulip.host.service;

import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.data.PayMonthSummary;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.Transaction;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.FeesLineItemRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransactionRepository;
import com.tulip.host.web.rest.vm.PayVM;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private TransactionRepository transactionRepository;

    private TransactionMapper transactionMapper;

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

    public List<PayMonthSummary> yearFeesSummary(Long sessionId, Long studentId) {
        return feesLineItemRepository.fetchTuitionFeesSummary(studentId, sessionId);
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
