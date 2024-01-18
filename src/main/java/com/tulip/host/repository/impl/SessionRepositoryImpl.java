package com.tulip.host.repository.impl;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.domain.Session;
import com.tulip.host.repository.SessionRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class SessionRepositoryImpl extends BaseRepositoryImpl<Session, Long> implements SessionRepository {

    protected SessionRepositoryImpl(EntityManager em) {
        super(Session.class, em);
    }

    @Override
    public Session fetchCurrentSession() {
        return jpaQueryFactory.selectFrom(SESSION).where(expressionBetweenDate(LocalDate.now())).fetchFirst();
    }

    @Override
    public List<Session> listAllFinancialYears() {
        return jpaQueryFactory.selectFrom(SESSION).fetch();
    }

    public Session sessionByDate(Date date) {
        return jpaQueryFactory.selectFrom(SESSION).where(expressionBetweenDate(LocalDate.now())).fetchFirst();
    }

    private BooleanExpression expressionBetweenDate(LocalDate date) {
        return Expressions.booleanOperation(Ops.BETWEEN, Expressions.asDate(date), SESSION.fromDate, SESSION.toDate);
    }
}
