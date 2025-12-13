package com.tulip.host.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tulip.host.data.LeaveBalanceDTO;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.domain.Session;
import com.tulip.host.enums.LeaveStatus;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Long> {

    Map<String, List<LeaveBalanceDTO>> findLeaveBalance();
    List<EmployeeLeave> findByEmployeeId(Long employeeId);

    List<LeaveBalanceDTO> findLeaveBalance(Long employeeId);

    List<LeaveBalanceDTO> findLeaveBalanceByTid(String tid);

    List<EmployeeLeave> findBySession(Session session);

    List<EmployeeLeave> findByDateRange(String fromDate, String toDate);

    List<EmployeeLeave> findByStatus(LeaveStatus status);
}
