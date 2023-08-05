package com.tulip.host.service;

import com.tulip.host.domain.Session;
import com.tulip.host.repository.ExpenseRepository;
import com.tulip.host.repository.StudentRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisualizeService {

    private final StudentRepository studentRepository;

    private final SessionService sessionService;

    private final ExpenseRepository expenseRepository;

    public Map<String, Long> admissionByMonth() {
        Session session = sessionService.currentSession();
        return studentRepository.admissionStats(session.getFromDate(), session.getToDate());
    }

    public Map<String, Map<String, Double>> getExpenseRecord() {
        Session session = sessionService.currentSession();
        return expenseRepository.expenseReport(session.getFromDate(), session.getToDate());
    }
}
