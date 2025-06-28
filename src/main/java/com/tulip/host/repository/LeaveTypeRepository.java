package com.tulip.host.repository;

import com.tulip.host.domain.LeaveType;
import com.tulip.host.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    boolean existsByName(String name);

    List<LeaveType> findBySession(Session session);
}
