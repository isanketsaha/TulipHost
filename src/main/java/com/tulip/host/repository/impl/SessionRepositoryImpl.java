package com.tulip.host.repository.impl;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.data.SessionDTO;
import com.tulip.host.domain.Session;
import com.tulip.host.repository.SessionRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;

public class SessionRepositoryImpl extends BaseRepositoryImpl<Session, Long> implements SessionRepository {

    protected SessionRepositoryImpl(EntityManager em) {
        super(Session.class, em);
    }

    @Override
    public Session fetchCurrentSession() {
        final BooleanExpression operation;
        try {
            operation =
                Expressions.booleanOperation(
                    Ops.BETWEEN,
                    Expressions.asDate(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))),
                    SESSION.fromDate,
                    SESSION.toDate
                );
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return jpaQueryFactory.selectFrom(SESSION).where(operation).fetchFirst();
    }

    @Override
    public List<Session> listAllFinancialYears() {
        return jpaQueryFactory.selectFrom(SESSION).fetch();
    }
}
