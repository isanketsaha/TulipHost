package com.tulip.host.service;

import static com.tulip.host.utils.CommonUtils.convertToLocalDate;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Session;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.ClassToStudentRepository;
import com.tulip.host.repository.ExpenseRepository;
import com.tulip.host.repository.StudentRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Months;
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

    public Map<String, Long> admissionByMonth() {
        Session session = sessionService.currentSession();
        return studentRepository.admissionStats(session.getFromDate(), session.getToDate());
    }

    public Map<String, Map<String, Double>> getExpenseRecord() {
        Session session = sessionService.currentSession();
        return expenseRepository.expenseReport(session.getFromDate(), session.getToDate());
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
        Map<String, Double> revenueYearly = classToStudentRepository.recurringRevenueYearly(session);
        Map<String, Long> initialSessionStrength = classToStudentRepository.initialSessionStrength(session, session.getFromDate());
        Map<String, Map<String, Long>> admissionOverYear = classToStudentRepository.overYearAdmission(session);
        Map<String, Double> feesByClass = classDetailRepository.getMonthlyFeesByClass(session);
        Map<String, Double> tuitionFees = new HashMap<>();
        double admissionCharges = 0.0;
        Map<String, Double> admissionFeesByClass = classDetailRepository.getAdmissionFeesByClass(session);

        for (Map.Entry<String, Map<String, Long>> entry : admissionOverYear.entrySet()) {
            String std = entry.getKey();
            Map<String, Long> admissionByMonth = entry.getValue();
            LocalDate startDate = convertToLocalDate(session.getFromDate());
            LocalDate endDate = convertToLocalDate(session.getToDate());
            long studentCount = 0;
            while (startDate.isBefore(endDate)) {
                if (initialSessionStrength.containsKey(std)) {
                    studentCount +=
                        initialSessionStrength.get(std) + admissionByMonth.getOrDefault(yearMonthFormatter.format(startDate), 0L);
                    initialSessionStrength.remove(std);
                } else {
                    studentCount += admissionByMonth.getOrDefault(yearMonthFormatter.format(startDate), 0L);
                }
                admissionCharges += admissionByMonth.getOrDefault(yearMonthFormatter.format(startDate), 0L) * admissionFeesByClass.get(std);
                tuitionFees.put(std, tuitionFees.getOrDefault(std, 0.0) + (studentCount * feesByClass.get(std)));
                startDate = startDate.plusMonths(1);
            }
        }
        log.info("Fees : {} - {} ", initialSessionStrength, tuitionFees);
        initialSessionStrength.forEach((k, v) ->
            tuitionFees.put(
                k,
                (v * feesByClass.get(k)) *
                (ChronoUnit.MONTHS.between(convertToLocalDate(session.getFromDate()), convertToLocalDate(session.getToDate())) + 1)
            )
        );
        log.info("{} - {} - {} ", tuitionFees, revenueYearly, admissionCharges);
        log.info(
            "Months - {}",
            ChronoUnit.MONTHS.between(convertToLocalDate(session.getFromDate()), convertToLocalDate(session.getToDate()))
        );
        return (
            tuitionFees.values().stream().reduce(0.0, Double::sum) +
            revenueYearly.values().stream().reduce(0.0, Double::sum) +
            admissionCharges
        );
    }
}
