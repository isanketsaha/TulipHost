package com.tulip.host.repository.impl;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.sum;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Session;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.repository.ClassDetailRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassDetailRepositoryImpl extends BaseRepositoryImpl<ClassDetail, Long> implements ClassDetailRepository {

    protected ClassDetailRepositoryImpl(EntityManager em) {
        super(ClassDetail.class, em);
    }

    @Override
    public List<ClassDetail> findAllBySessionId(Long sessionId) {
        return jpaQueryFactory.selectFrom(CLASS_DETAIL).where(CLASS_DETAIL.session().id.eq(sessionId)).fetch();
    }

    @Override
    public ClassDetail findBySessionIdAndStd(Long sessionId, String std) {
        return jpaQueryFactory
            .selectFrom(CLASS_DETAIL)
            .where(CLASS_DETAIL.session().id.eq(sessionId).and(CLASS_DETAIL.std.eq(std)))
            .fetchOne();
    }

    @Override
    public ClassDetail findByClass(Long classId) {
        return jpaQueryFactory.selectFrom(CLASS_DETAIL).distinct().where(CLASS_DETAIL.id.eq(classId)).fetchOne();
    }

    @Override
    public Map getMonthlyFeesByClass(Session session) {
        return jpaQueryFactory
            .select(CLASS_DETAIL.std, FEES_CATALOG.price.sum())
            .from(CLASS_DETAIL)
            .innerJoin(CLASS_DETAIL.feesCatalogs, FEES_CATALOG)
            .on(FEES_CATALOG.active.eq(true))
            .where(
                CLASS_DETAIL
                    .session()
                    .eq(session)
                    .and(
                        FEES_CATALOG.applicableRule
                            .eq(FeesRuleType.MONTHLY)
                            .and(FEES_CATALOG.feesName.notLike(Expressions.asString("%").concat("LATE").concat("%")))
                    )
            )
            .transform(groupBy(CLASS_DETAIL.std).as(sum(FEES_CATALOG.price)));
    }
}
