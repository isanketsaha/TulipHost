package com.tulip.host.repository;

import com.tulip.host.domain.AppraisalSelectedParameter;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppraisalSelectedParameterRepository extends JpaRepository<AppraisalSelectedParameter, Long> {
    List<AppraisalSelectedParameter> findByAppraisalId(Long appraisalId);

    void deleteByAppraisalId(Long appraisalId);
}
