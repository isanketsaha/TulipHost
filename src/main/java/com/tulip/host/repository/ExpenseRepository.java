package com.tulip.host.repository;

import com.tulip.host.domain.Expense;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Map<String, Map<String, Double>> expenseReport(LocalDate startDate, LocalDate endDate);
}
