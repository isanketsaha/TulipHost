package com.tulip.host.repository;

import com.tulip.host.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {}
