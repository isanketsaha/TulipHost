package com.tulip.host.repository;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Session;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassDetailRepository extends JpaRepository<ClassDetail, Long> {
    List<ClassDetail> findAllBySessionId(Long sessionId);

    ClassDetail findBySessionIdAndStd(Long sessionId, String std);

    ClassDetail findByClass(Long classId);

    Map<String, Double> getMonthlyFeesByClass(Session session);

    Map<String, Double> getAdmissionFeesByClass(Session session);
}
