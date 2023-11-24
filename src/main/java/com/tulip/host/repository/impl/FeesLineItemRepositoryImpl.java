package com.tulip.host.repository.impl;

import com.tulip.host.data.PayMonthSummary;
import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.Session;
import com.tulip.host.repository.FeesLineItemRepository;
import jakarta.persistence.EntityManager;
import java.util.List;

public class FeesLineItemRepositoryImpl extends BaseRepositoryImpl<FeesLineItem, Long> implements FeesLineItemRepository {

    protected FeesLineItemRepositoryImpl(EntityManager em) {
        super(FeesLineItem.class, em);
    }

    @Override
    public List<PayMonthSummary> fetchTuitionFeesSummary(Long studentId, Long sessionId) {
        return null;
    }

    @Override
    public List<String> fetchTransportMonths(Long studentId, Session sessionId) {
        return jpaQueryFactory
            .select(FEES_LINE_ITEM.month)
            .from(FEES_LINE_ITEM)
            .join(FEES_LINE_ITEM.transport(), TRANSPORT_CATALOG)
            .on(TRANSPORT_CATALOG.session().eq(sessionId))
            .where(FEES_LINE_ITEM.transport().isNotNull())
            .fetch();
    }

    @Override
    public List<FeesLineItem> checkIfTransportPaid(Long studentId, Long transportId, String month) {
        return jpaQueryFactory
            .selectFrom(FEES_LINE_ITEM)
            .where(
                FEES_LINE_ITEM
                    .transport()
                    .isNotNull()
                    .and(FEES_LINE_ITEM.transport().id.eq(transportId))
                    .and(FEES_LINE_ITEM.month.eq(month))
            )
            .fetch();
    }
}
