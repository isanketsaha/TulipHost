package com.tulip.host.repository.impl;

import com.querydsl.core.Tuple;
import com.tulip.host.domain.Transaction;
import com.tulip.host.repository.TransactionRepository;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

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
    public Map<String, Long> fetchTransactionGroupBy(Date from, Date to) {
        List<Tuple> fetch = jpaQueryFactory
            .selectFrom(TRANSACTION)
            .where(TRANSACTION.createdDate.between(from, to))
            .groupBy(TRANSACTION.type)
            .select(TRANSACTION.amount.sum(), TRANSACTION.type)
            .fetch();
        return null;
    }
}
