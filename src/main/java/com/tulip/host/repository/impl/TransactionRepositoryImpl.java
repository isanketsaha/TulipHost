package com.tulip.host.repository.impl;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.map;
import static com.tulip.host.config.Constants.DATE_PATTERN;
import static com.tulip.host.config.Constants.GROUP_BY_MONTH_FORMAT;
import static com.tulip.host.config.Constants.MONTH_YEAR_PATTERN;
import static com.tulip.host.config.Constants.MONTH_YEAR_FORMAT;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.tulip.host.data.TransactionReportDTO;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Transaction;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.Period;
import com.tulip.host.domain.Student;
import com.tulip.host.utils.CommonUtils;

public class TransactionRepositoryImpl extends BaseRepositoryImpl<Transaction, Long> implements TransactionRepository {

    private final String dateFormat = "DATE_FORMAT({0}, 'groupBy')";

    protected TransactionRepositoryImpl(EntityManager em) {
        super(Transaction.class, em);
    }

    @Override
    public Double fetchTransactionTotal(LocalDate from, LocalDate to) {
        return jpaQueryFactory
            .selectFrom(TRANSACTION)
            .where(TRANSACTION.createdDate.between(from.atStartOfDay(), to.atTime(LocalTime.MAX)))
            .select(TRANSACTION.amount.sum())
            .fetchOne();
    }

    @Override
    public List<TransactionReportDTO> fetchTransactionGroupBy(LocalDate from, LocalDate to, String groupByFormat) {
        StringExpression monthYear = Expressions.stringTemplate(dateFormat.replace("groupBy", groupByFormat), TRANSACTION.createdDate);

        List<Tuple> tupleList = jpaQueryFactory
            .select(monthYear, TRANSACTION.amount.sum(), TRANSACTION.type)
            .from(TRANSACTION)
            .where(TRANSACTION.createdDate.between(from.atStartOfDay(), to.atTime(LocalTime.MAX)))
            .groupBy(monthYear, TRANSACTION.type)
            .fetch();

        Map<String, TransactionReportDTO> transactionReportMap = new HashMap<>();
        for (Tuple tuple : tupleList) {
            String monthYearValue = tuple.get(monthYear);
            TransactionReportDTO transactionReport = transactionReportMap.getOrDefault(monthYearValue, new TransactionReportDTO());
            transactionReport.setTransactionDate(
                groupByFormat.equalsIgnoreCase(GROUP_BY_MONTH_FORMAT)
                    ? YearMonth.parse(monthYearValue, DateTimeFormatter.ofPattern(MONTH_YEAR_PATTERN, Locale.ENGLISH)).atEndOfMonth()
                    : LocalDate.parse(monthYearValue, DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.ENGLISH))
            );
            mapTransactionType(transactionReport, tuple);
            transactionReportMap.put(monthYearValue, transactionReport);
        }

        return new ArrayList<>(transactionReportMap.values());
    }

    @Override
    public List<String> fetchAnnualFeesByClass(Long studentId, Long classId) {
        return jpaQueryFactory
                .select(FEES_LINE_ITEM.month)
                .from(STUDENT)
                .innerJoin(STUDENT.transactions, TRANSACTION)
            .innerJoin(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .innerJoin(FEES_LINE_ITEM.feesProduct(), FEES_CATALOG)
            .where(
                        STUDENT.id.eq(studentId)
                                .and(FEES_CATALOG.std().id.eq(classId))
                                .and(TRANSACTION.type.eq("FEES"))
                                .and(FEES_LINE_ITEM.month
                                        .isNotNull())
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

    public List<Transaction> fetchAllTransactionByDues() {
        return jpaQueryFactory
            .selectFrom(TRANSACTION)
            .innerJoin(TRANSACTION.dues(), DUES)
                .where(DUES.status.eq("ACTIVE").and(TRANSACTION.student().active.eq(true)))
            .orderBy(new OrderSpecifier[] { TRANSACTION.createdDate.desc() })
            .fetch();
    }

    public Map<String, Map<String, Double>> fetchSalesReport(LocalDate date) {
        return jpaQueryFactory
            .selectFrom(TRANSACTION)
            .where(TRANSACTION.createdDate.between(date.atStartOfDay(), date.atTime(LocalTime.MAX)))
            .groupBy(TRANSACTION.type, TRANSACTION.paymentMode)
            .transform(groupBy(TRANSACTION.type).as(map(TRANSACTION.paymentMode, TRANSACTION.amount.sum())));
    }

    @Override
    public List<String> fetchTransportMonths(Long studentId, Session sessionId) {
        return jpaQueryFactory
            .select(FEES_LINE_ITEM.month)
            .from(TRANSACTION)
            .join(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .on(FEES_LINE_ITEM.transport().isNotNull())
            .join(FEES_LINE_ITEM.transport(), TRANSPORT_CATALOG)
            .on(TRANSPORT_CATALOG.session().eq(sessionId))
            .where(TRANSACTION.student().id.eq(studentId))
            .fetch();
    }

    @Override
    public List<Transaction> checkIfTransportPaid(Long studentId, Long transportId, String month) {
        return jpaQueryFactory
            .selectFrom(TRANSACTION)
            .join(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .on(FEES_LINE_ITEM.transport().isNotNull())
            .join(TRANSACTION.student(), STUDENT)
            .on(STUDENT.id.eq(studentId))
            .where(
                FEES_LINE_ITEM
                    .transport()
                    .isNotNull()
                    .and(FEES_LINE_ITEM.transport().id.eq(transportId))
                    .and(FEES_LINE_ITEM.month.eq(month))
            )
            .fetch();
    }

    @Override
    public List<Object[]> fetchPendingFeesBatch(List<Long> studentIds, Long classId, Long sessionId) {
        // Get the latest tuition fees payment date for each student
        List<Tuple> latestPaymentResults = jpaQueryFactory
                .select(STUDENT.id, FEES_LINE_ITEM.month.max())
                .from(STUDENT)
                .leftJoin(STUDENT.transactions, TRANSACTION)
                .leftJoin(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
                .leftJoin(FEES_LINE_ITEM.feesProduct(), FEES_CATALOG)
                .where(
                        STUDENT.id.in(studentIds)
                                .and(FEES_CATALOG.std().id.eq(classId))
                                .and(TRANSACTION.type.eq("FEES"))
                                .and(FEES_CATALOG.applicableRule.eq(FeesRuleType.MONTHLY))
                                .and(FEES_CATALOG.feesName.eq("TUITION FEES"))
                                .and(FEES_LINE_ITEM.month.isNotNull()))
                .groupBy(STUDENT.id)
                .fetch();

        // Get session details for calculating pending months
        Session session = jpaQueryFactory
                .selectFrom(SESSION)
                .where(SESSION.id.eq(sessionId))
                .fetchOne();

        List<Object[]> results = new ArrayList<>();

        for (Tuple tuple : latestPaymentResults) {
            Long studentId = tuple.get(STUDENT.id);
            String latestMonth = tuple.get(FEES_LINE_ITEM.month.max());

            int pendingMonths = 0;
            if (latestMonth != null) {
                // Parse the latest month and calculate pending months
                LocalDate latestPaymentDate = YearMonth
                        .parse(latestMonth, DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT)).atEndOfMonth();
                LocalDate currentDate = LocalDate.now();
                LocalDate cutoffDate = currentDate.withDayOfMonth(20);

                // If current date is past 20th, fees for current month is pending
                if (currentDate.isAfter(cutoffDate)) {
                    cutoffDate = currentDate.withDayOfMonth(1).plusMonths(1).withDayOfMonth(20);
                }

                pendingMonths = Period.between(latestPaymentDate, cutoffDate).getMonths();
            } else {
                // No payments found, calculate from session start or student creation date
                Student student = jpaQueryFactory
                        .selectFrom(STUDENT)
                        .where(STUDENT.id.eq(studentId))
                        .fetchOne();

                LocalDateTime startDate = student.getCreatedDate().isBefore(session.getFromDate().atStartOfDay())
                        ? session.getFromDate().atStartOfDay()
                        : student.getCreatedDate();

                LocalDate currentDate = LocalDate.now();
                LocalDate cutoffDate = currentDate.withDayOfMonth(20);

                // If current date is past 20th, fees for current month is pending
                if (currentDate.isAfter(cutoffDate)) {
                    cutoffDate = currentDate.withDayOfMonth(1).plusMonths(1).withDayOfMonth(20);
                }

                pendingMonths = Period.between(startDate.withDayOfMonth(1).toLocalDate(), cutoffDate).getMonths();
            }

            results.add(new Object[] { studentId, pendingMonths });
        }

        return results;
    }

    @Override
    public Map<Long, List<String>> fetchAnnualFeesByClassBatch(List<Long> studentIds, Long classId) {
        List<Tuple> results = jpaQueryFactory
                .select(STUDENT.id, FEES_CATALOG.feesName)
                .from(STUDENT)
                .innerJoin(STUDENT.transactions, TRANSACTION)
                .innerJoin(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
                .innerJoin(FEES_LINE_ITEM.feesProduct(), FEES_CATALOG)
                .where(
                        STUDENT.id.in(studentIds)
                                .and(FEES_CATALOG.std().id.eq(classId))
                                .and(TRANSACTION.type.eq("FEES"))
                                .and(FEES_CATALOG.applicableRule.eq(FeesRuleType.YEARLY))
                                .and(FEES_LINE_ITEM.month.isNull()))
                .fetch();

        Map<Long, List<String>> resultMap = new HashMap<>();
        for (Tuple tuple : results) {
            Long studentId = tuple.get(STUDENT.id);
            String feesName = tuple.get(FEES_CATALOG.feesName);
            resultMap.computeIfAbsent(studentId, k -> new ArrayList<>()).add(feesName);
        }

        return resultMap;
    }

    @Override
    public List<Transaction> fetchAllTransactionByDuesWithLimit(int limit) {
        return jpaQueryFactory
                .selectFrom(TRANSACTION)
                .innerJoin(TRANSACTION.dues(), DUES)
                .where(DUES.status.ne("PAID").and(TRANSACTION.student().active.eq(true)))
                .orderBy(DUES.dueDate.asc())
                .limit(limit)
                .fetch();
    }
}
