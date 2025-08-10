package com.tulip.host.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tulip.host.data.LeaveBalanceDTO;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.domain.Session;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Long> {

    List<EmployeeLeave> findByEmployeeId(Long employeeId);

    List<LeaveBalanceDTO> findLeaveBalance(Long employeeId);

    List<EmployeeLeave> findBySession(Session session);

    List<EmployeeLeave> findByDateRange(String fromDate, String toDate);
}
