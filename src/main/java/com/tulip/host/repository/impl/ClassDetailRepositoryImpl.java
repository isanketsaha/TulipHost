package com.tulip.host.repository.impl;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.sum;

import com.querydsl.core.Tuple;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Session;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.utils.CommonUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
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
            .select(CLASS_DETAIL.std, FEES_CATALOG.price, FEES_CATALOG.applicableRule)
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
                            .and(FEES_CATALOG.feesName.notEqualsIgnoreCase("LATE PAYMENT FEES"))
                    )
            )
            .transform(groupBy(CLASS_DETAIL.std).as(FEES_CATALOG.price));
    }

    public Map<String, Double> getAdmissionFeesByClass(Session session) {
        return jpaQueryFactory
            .select(CLASS_DETAIL.std, FEES_CATALOG.price)
            .from(CLASS_DETAIL)
            .innerJoin(CLASS_DETAIL.feesCatalogs, FEES_CATALOG)
            .on(FEES_CATALOG.active.eq(true))
            .where(
                CLASS_DETAIL
                    .session()
                    .eq(session)
                    .and(FEES_CATALOG.applicableRule.eq(FeesRuleType.YEARLY).and(FEES_CATALOG.feesName.notEqualsIgnoreCase("SESSION FEES")))
            )
            .transform(groupBy(CLASS_DETAIL.std).as(sum(FEES_CATALOG.price)));
    }
}
