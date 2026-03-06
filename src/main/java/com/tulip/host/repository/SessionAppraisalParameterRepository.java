package com.tulip.host.repository;

import com.tulip.host.domain.SessionAppraisalParameter;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionAppraisalParameterRepository extends JpaRepository<SessionAppraisalParameter, Long> {
    List<SessionAppraisalParameter> findBySessionId(Long sessionId);

    void deleteBySessionId(Long sessionId);
}
