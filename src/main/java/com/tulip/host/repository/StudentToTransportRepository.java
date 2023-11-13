package com.tulip.host.repository;

import com.tulip.host.domain.Session;
import com.tulip.host.domain.StudentToTransport;
import com.tulip.host.domain.StudentToTransportId;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentToTransportRepository extends JpaRepository<StudentToTransport, StudentToTransportId> {
    Map<String, Long> findReport(Session session);
}
