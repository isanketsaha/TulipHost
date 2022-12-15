package com.tulip.host.repository;

import com.tulip.host.domain.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineItemRepository extends JpaRepository<LineItem, Long> {}
