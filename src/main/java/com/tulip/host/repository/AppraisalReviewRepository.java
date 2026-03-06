package com.tulip.host.repository;

import com.tulip.host.domain.AppraisalReview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppraisalReviewRepository extends JpaRepository<AppraisalReview, Long> {
    List<AppraisalReview> findByAppraisalIdOrderByStartDateAsc(Long appraisalId);
}
