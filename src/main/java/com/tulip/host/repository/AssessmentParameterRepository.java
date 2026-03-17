package com.tulip.host.repository;

import com.tulip.host.domain.AssessmentParameter;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentParameterRepository extends JpaRepository<AssessmentParameter, Long> {
    List<AssessmentParameter> findByActiveTrue();
}
