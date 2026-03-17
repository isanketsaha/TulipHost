package com.tulip.host.repository;

import com.tulip.host.domain.AppraisalParameter;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppraisalParameterRepository extends JpaRepository<AppraisalParameter, Long> {
    List<AppraisalParameter> findByActiveTrue();
}
