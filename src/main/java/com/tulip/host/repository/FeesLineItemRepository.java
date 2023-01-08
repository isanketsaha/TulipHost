package com.tulip.host.repository;

import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.PurchaseLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeesLineItemRepository extends JpaRepository<FeesLineItem, Long> {}
