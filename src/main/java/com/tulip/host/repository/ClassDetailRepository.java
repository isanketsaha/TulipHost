package com.tulip.host.repository;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.domain.ClassDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassDetailRepository extends JpaRepository<ClassDetail, Long> {
    List<ClassDetail> findAllBySessionId(Long sessionId);

    ClassDetail findBySessionIdAndStd(Long sessionId, Long std);
}
