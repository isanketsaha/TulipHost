package com.tulip.host.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.tulip.host.data.PayMonthSummary;
import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.repository.FeesLineItemRepository;
import java.util.List;
import javax.persistence.EntityManager;

public class FeesLineItemRepositoryImpl extends BaseRepositoryImpl<FeesLineItem, Long> implements FeesLineItemRepository {

    protected FeesLineItemRepositoryImpl(EntityManager em) {
        super(FeesLineItem.class, em);
    }

    @Override
    public List<PayMonthSummary> fetchTuitionFeesSummary(Long studentId, Long sessionId) {
        //        return jpaQueryFactory
        //            .select(
        //                Projections.fields(
        //                    PayMonthSummary.class,
        //                    STUDENT.createdDate,
        //                    FEES_LINE_ITEM.fromMonth,
        //                    FEES_LINE_ITEM.toMonth,
        //                    FEES_CATALOG.feesName
        //                )
        //            )
        //            .from(FEES_LINE_ITEM)
        //            .join(FEES_CATALOG)
        //            .on(FEES_CATALOG.id.eq(FEES_LINE_ITEM.id)
        //                .and(FEES_CATALOG.applicableRule
        //                    .containsIgnoreCase("MONTHLY")))
        //            .join(STUDENT)
        //            .on(STUDENT.id.eq(studentId)
        //                .and(STUDENT.feesOrder.any().lineItem.contains(FEES_LINE_ITEM)))
        //            .join(CLASS_DETAIL)
        //            .on(STUDENT.std()
        //                .eq(CLASS_DETAIL))
        //            .join(SESSION)
        //            .on(CLASS_DETAIL.session()
        //                .eq(SESSION)
        //                .and(SESSION.id.eq(sessionId)))
        //            .fetch();
        return null;
    }
}
