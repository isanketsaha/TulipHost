package com.tulip.host.repository;

import com.tulip.host.domain.Expense;
import java.util.Date;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Map<String, Map<String, Double>> expenseReport(Date startDate, Date endDate);
}
