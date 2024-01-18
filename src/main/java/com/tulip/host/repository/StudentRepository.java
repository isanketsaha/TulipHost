package com.tulip.host.repository;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.domain.Student;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> fetchAll(boolean isActive);

    public List<Student> search(String name);

    Student search(long id);

    Student searchByClassId(long studentId, long classId);

    long fetchStudentCount(boolean active, BooleanBuilder condition);

    Student checkIfFeesPaid(Long studentId, Long feesId, String month);

    Map<String, Long> admissionStats(LocalDate startDate, LocalDate endDate);
}
