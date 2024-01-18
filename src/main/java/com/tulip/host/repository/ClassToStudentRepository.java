package com.tulip.host.repository;

import com.tulip.host.domain.Session;
import com.tulip.host.domain.StudentToClass;
import com.tulip.host.domain.StudentToClassId;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassToStudentRepository extends JpaRepository<StudentToClass, StudentToClassId> {
    Map<String, Map<String, Long>> overYearAdmission(Session session);

    Map<String, Double> recurringRevenueYearly(Session session);

    Map<String, Long> initialSessionStrength(Session session, LocalDate date);
}
