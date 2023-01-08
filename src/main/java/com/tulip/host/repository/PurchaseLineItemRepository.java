package com.tulip.host.repository;

import com.tulip.host.domain.PurchaseLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseLineItemRepository extends JpaRepository<PurchaseLineItem, Long> {}
