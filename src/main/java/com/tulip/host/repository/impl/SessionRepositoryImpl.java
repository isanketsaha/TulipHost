package com.tulip.host.repository.impl;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.data.pojo.SessionPojo;
import com.tulip.host.domain.Session;
import com.tulip.host.repository.SessionRepository;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;

public class SessionRepositoryImpl extends BaseRepositoryImpl<Session, Long> implements SessionRepository {

    protected SessionRepositoryImpl(EntityManager em) {
        super(Session.class, em);
    }

    @Override
    public Optional<SessionPojo> fetchCurrentSession() {
        final BooleanExpression operation = Expressions.booleanOperation(
            Ops.BETWEEN,
            Expressions.asDate(LocalDate.now()),
            SESSION.fromDate,
            SESSION.toDate
        );
        return Optional.ofNullable(
            jpaQueryFactory
                .from(SESSION)
                .where(operation)
                .select(Projections.fields(SessionPojo.class, SESSION.id, SESSION.displayText))
                .fetchFirst()
        );
    }

    @Override
    public List<SessionPojo> listAllFinancialYears() {
        return jpaQueryFactory.from(SESSION).select(Projections.fields(SessionPojo.class, SESSION.id, SESSION.displayText)).fetch();
    }
}
