package com.tulip.host.repository.impl;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.StudentToTransport;
import com.tulip.host.domain.StudentToTransportId;
import com.tulip.host.repository.StudentToTransportRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class StudentToTransportRepositoryImpl
    extends BaseRepositoryImpl<StudentToTransport, StudentToTransportId>
    implements StudentToTransportRepository {

    protected StudentToTransportRepositoryImpl(EntityManager em) {
        super(StudentToTransport.class, em);
    }

    public Map<String, List<Long>> findReport(Session session) {
        return jpaQueryFactory
            .selectFrom(STUDENT_TO_TRANSPORT)
            .innerJoin(TRANSPORT_CATALOG)
            .on(
                TRANSPORT_CATALOG
                    .session()
                    .eq(session)
                    .and(STUDENT_TO_TRANSPORT.startDate.before(Instant.now()))
                    .and(
                        (STUDENT_TO_TRANSPORT.endDate.isNull()).or(
                                Expressions.asDateTime(Instant.now()).between(STUDENT_TO_TRANSPORT.startDate, STUDENT_TO_TRANSPORT.endDate)
                            )
                    )
            )
            .groupBy(STUDENT_TO_TRANSPORT.student(), STUDENT_TO_TRANSPORT.transport())
            .transform(groupBy(STUDENT_TO_TRANSPORT.transport().location).as(list(STUDENT_TO_TRANSPORT.student().id)));
    }
}
