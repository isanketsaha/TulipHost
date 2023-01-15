package com.tulip.host.repository;

import com.tulip.host.data.PayMonthSummary;
import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.PurchaseLineItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeesLineItemRepository extends JpaRepository<FeesLineItem, Long> {
    public List<PayMonthSummary> fetchTuitionFeesSummary(Long studentId, Long sessionId);
}
