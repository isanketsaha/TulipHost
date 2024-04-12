package com.tulip.host.service;

import com.tulip.host.domain.Session;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.ClassToStudentRepository;
import com.tulip.host.repository.ExpenseRepository;
import com.tulip.host.repository.StudentRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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

    private final ClassDetailRepository classDetailRepository;

    private final ClassToStudentRepository classToStudentRepository;

    @Transactional
    public Map<String, Long> admissionByMonth() {
        Session session = sessionService.currentSession();
        return studentRepository.admissionStats(session.getFromDate(), session.getToDate());
    }

    @Transactional
    public Map<String, Map<String, Double>> getExpenseRecord() {
        return expenseRepository.expenseReport(LocalDate.now().minus(5, ChronoUnit.MONTHS), LocalDate.now());
    }

    @Transactional
    public double getMRR() {
        Session session = sessionService.currentSession();
        Map<String, Double> feesByClass = classDetailRepository.getMonthlyFeesByClass(session);
        Map<String, Long> classSize = classToStudentRepository.initialSessionStrength(session, session.getToDate());
        return feesByClass
            .entrySet()
            .stream()
            .filter(entry -> classSize.containsKey(entry.getKey()))
            .mapToDouble(entry -> entry.getValue() * classSize.get(entry.getKey()))
            .sum();
    }

    public double getYRR() {
        Session session = sessionService.currentSession();
        DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");
        Map<String, Long> initialSessionStrength = classToStudentRepository.initialSessionStrength(session, session.getFromDate());
        Map<String, Double> paymentDone = classToStudentRepository.paymentDoneTillDate(session);
        Map<String, Double> feesByClass = classDetailRepository.getMonthlyFeesByClass(session);
        double forecast = feesByClass
            .entrySet()
            .stream()
            .filter(entry -> initialSessionStrength.containsKey(entry.getKey()))
            .mapToDouble(entry ->
                entry.getValue() *
                initialSessionStrength.get(entry.getKey()) *
                Period.between(LocalDate.now(), session.getToDate()).getMonths()
            )
            .sum();
        double paidTillDate = paymentDone.entrySet().stream().mapToDouble(entry -> entry.getValue()).sum();
        return forecast + paidTillDate;
    }
}
