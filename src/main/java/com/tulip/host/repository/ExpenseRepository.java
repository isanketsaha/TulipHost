package com.tulip.host.repository;

import com.tulip.host.domain.Expense;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExpenseRepository extends PagingAndSortingRepository<Expense, Long> {}
