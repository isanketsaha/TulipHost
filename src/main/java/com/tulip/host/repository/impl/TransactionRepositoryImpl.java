package com.tulip.host.repository.impl;

import static com.tulip.host.config.Constants.GROUP_BY_MONTH_FORMAT;
import static com.tulip.host.utils.CommonUtils.formatToDate;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.tulip.host.data.TransactionReportDTO;
import com.tulip.host.domain.Transaction;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.enums.PaymentOptionEnum;
import com.tulip.host.repository.TransactionRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Pageable;

public class TransactionRepositoryImpl extends BaseRepositoryImpl<Transaction, Long> implements TransactionRepository {

    private final String dateFormat = "DATE_FORMAT({0}, 'groupBy')";

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
    public List<TransactionReportDTO> fetchTransactionGroupBy(Date from, Date to, String groupByFormat) {
        StringExpression monthYear = Expressions.stringTemplate(dateFormat.replace("groupBy", groupByFormat), TRANSACTION.createdDate);

        List<Tuple> tupleList = jpaQueryFactory
            .select(monthYear, TRANSACTION.amount.sum(), TRANSACTION.type)
            .from(TRANSACTION)
            .where(TRANSACTION.createdDate.between(from, to))
            .groupBy(monthYear, TRANSACTION.type)
            .fetch();

        Map<String, TransactionReportDTO> transactionReportMap = new HashMap<>();
        for (Tuple tuple : tupleList) {
            String monthYearValue = tuple.get(monthYear);
            TransactionReportDTO transactionReport = transactionReportMap.getOrDefault(monthYearValue, new TransactionReportDTO());
            transactionReport.setTransactionDate(
                formatToDate(monthYearValue, groupByFormat.equalsIgnoreCase(GROUP_BY_MONTH_FORMAT) ? "MM/yyyy" : "dd/MM/yyyy")
            );
            mapTransactionType(transactionReport, tuple);
            transactionReportMap.put(monthYearValue, transactionReport);
        }

        return new ArrayList<>(transactionReportMap.values());
    }

    @Override
    public List<String> fetchAnnualFeesByClass(long studentId, long classId) {
        return jpaQueryFactory
            .select(FEES_CATALOG.feesName)
            .from(TRANSACTION)
            .innerJoin(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .innerJoin(FEES_LINE_ITEM.feesProduct(), FEES_CATALOG)
            .where(
                TRANSACTION
                    .student()
                    .id.eq(studentId)
                    .and(FEES_CATALOG.std().id.eq(classId))
                    .and(FEES_CATALOG.applicableRule.eq(FeesRuleType.YEARLY))
            )
            .fetch();
    }

    @Override
    public List<Transaction> fetchStudentFeesTransactionByClassId(long studentId, long classId) {
        return jpaQueryFactory
            .selectFrom(TRANSACTION)
            .innerJoin(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .innerJoin(FEES_LINE_ITEM.feesProduct(), FEES_CATALOG)
            .where(
                TRANSACTION
                    .student()
                    .id.eq(studentId)
                    .and(TRANSACTION.type.eq(PayTypeEnum.FEES.name()))
                    .and(FEES_CATALOG.std().id.eq(classId))
                    .and(FEES_CATALOG.applicableRule.eq(FeesRuleType.MONTHLY))
            )
            .orderBy(new OrderSpecifier[] { TRANSACTION.createdDate.desc() })
            .fetch();
    }

    private void mapTransactionType(TransactionReportDTO reportDTO, Tuple tuple) {
        String type = tuple.get(TRANSACTION.type);
        reportDTO.setId(UUID.randomUUID().toString().replace("-", ""));
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
