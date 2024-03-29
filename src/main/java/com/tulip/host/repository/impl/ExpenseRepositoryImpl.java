package com.tulip.host.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.tulip.host.domain.Expense;
import com.tulip.host.repository.ExpenseRepository;
import com.tulip.host.utils.CommonUtils;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpenseRepositoryImpl extends BaseRepositoryImpl<Expense, Long> implements ExpenseRepository {

    protected ExpenseRepositoryImpl(EntityManager em) {
        super(Expense.class, em);
    }

    public Map<String, Map<String, Double>> expenseReport(LocalDate startDate, LocalDate endDate) {
        List<Tuple> tupleList = jpaQueryFactory
            .select(EXPENSE.category, EXPENSE.createdDate.month(), EXPENSE.amount.sum())
            .from(EXPENSE)
            .where(
                EXPENSE.createdDate
                    .between(startDate.atStartOfDay(), endDate.atStartOfDay())
                    .and(EXPENSE.createdBy.notEqualsIgnoreCase("Sanket Saha"))
            )
            .groupBy(EXPENSE.createdDate.year(), EXPENSE.createdDate.month(), EXPENSE.category)
            .orderBy(EXPENSE.createdDate.year().asc(), EXPENSE.createdDate.month().asc())
            .fetch();
        return formatResult(tupleList);
    }

    private Map<String, Map<String, Double>> formatResult(List<Tuple> tupleList) {
        Map<String, Map<String, Double>> resultAsMap = new LinkedHashMap<>();
        for (Tuple tuple : tupleList) {
            if (tuple != null) {
                String monthCreated = CommonUtils.getMonthName(tuple.get(1, Integer.class));
                Double amount = tuple.get(2, Double.class);
                String category = tuple.get(0, String.class);
                if (resultAsMap.containsKey(category)) {
                    Map<String, Double> amountByCategory = resultAsMap.get(category);
                    amountByCategory.put(monthCreated, amount);
                } else {
                    HashMap<String, Double> amountByCategory = new LinkedHashMap<>();
                    amountByCategory.put(monthCreated, amount);
                    resultAsMap.put(category, amountByCategory);
                }
            }
        }
        return resultAsMap;
    }
}
