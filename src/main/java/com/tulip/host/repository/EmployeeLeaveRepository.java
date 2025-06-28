package com.tulip.host.repository;

import com.tulip.host.domain.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.querydsl.core.Tuple;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Long> {

    List<EmployeeLeave> findByEmployeeId(String employeeId);

    Map<String, Long> findLeaveBalance(String employeeId);
}
