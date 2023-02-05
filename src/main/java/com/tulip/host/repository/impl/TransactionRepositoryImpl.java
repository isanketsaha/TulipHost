package com.tulip.host.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.data.TransactionReportDTO;
import com.tulip.host.domain.Transaction;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.enums.PaymentOptionEnum;
import com.tulip.host.repository.TransactionRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Pageable;

public class TransactionRepositoryImpl extends BaseRepositoryImpl<Transaction, Long> implements TransactionRepository {

    protected TransactionRepositoryImpl(EntityManager em) {
        super(Transaction.class, em);
    }

    @Override
    public Double fetchTransactionTotal(Date from, Date to) {
        return jpaQueryFactory
            .selectFrom(TRANSACTION)
            .where(TRANSACTION.createdDate.between(from, to))
            .select(TRANSACTION.amount.sum())
            .fetchOne();
    }

    @Override
    public List<TransactionReportDTO> fetchTransactionGroupBy(Date from, Date to) {
        DateExpression<Date> createdDate = Expressions.dateTemplate(Date.class, "CAST({0} as date)", TRANSACTION.createdDate);
        List<Tuple> tupleList = jpaQueryFactory
            .selectFrom(TRANSACTION)
            .where(TRANSACTION.createdDate.between(from, to))
            .groupBy(TRANSACTION.type, createdDate)
            .select(createdDate, TRANSACTION.amount.sum(), TRANSACTION.type)
            .fetch();
        Map<Date, TransactionReportDTO> transactionReportMap = new HashMap<>();
        for (Tuple tuple : tupleList) {
            if (transactionReportMap.containsKey(tuple.get(createdDate))) {
                TransactionReportDTO transactionReport = transactionReportMap.get(tuple.get(createdDate));
                mapTransactionType(transactionReport, tuple);
                transactionReportMap.put(tuple.get(createdDate), transactionReport);
            } else {
                TransactionReportDTO transactionReport = new TransactionReportDTO();
                transactionReport.setTransactionDate(tuple.get(createdDate));
                mapTransactionType(transactionReport, tuple);
                transactionReportMap.put(tuple.get(createdDate), transactionReport);
            }
        }
        return MapUtils.isNotEmpty(transactionReportMap) ? new ArrayList<>(transactionReportMap.values()) : Collections.emptyList();
    }

    private void mapTransactionType(TransactionReportDTO reportDTO, Tuple tuple) {
        String type = tuple.get(TRANSACTION.type);
        double amount = tuple.get(TRANSACTION.amount.sum());
        if (type != null) {
            if (type.equals(PayTypeEnum.PURCHASE.name())) {
                reportDTO.setPurchase(amount);
            } else if (type.equals(PayTypeEnum.FEES.name())) {
                reportDTO.setFees(amount);
            } else {
                reportDTO.setExpense(amount);
            }
            reportDTO.setTotal(reportDTO.getTotal() + amount);
        }
    }
}
