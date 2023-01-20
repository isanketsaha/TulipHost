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
        return null;
    }
}
