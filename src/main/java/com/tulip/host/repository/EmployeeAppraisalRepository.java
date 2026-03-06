package com.tulip.host.repository;

import com.tulip.host.domain.EmployeeAppraisal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeAppraisalRepository extends JpaRepository<EmployeeAppraisal, Long> {
    List<EmployeeAppraisal> findByEmployeeIdOrderByCreatedDateDesc(Long employeeId);

    Optional<EmployeeAppraisal> findByEmployeeIdAndSessionId(Long employeeId, Long sessionId);
}
