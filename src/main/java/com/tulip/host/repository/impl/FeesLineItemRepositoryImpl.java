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
}
