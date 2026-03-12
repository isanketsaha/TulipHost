package com.tulip.host.repository;

import com.tulip.host.domain.StudentExamScore;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentExamScoreRepository extends JpaRepository<StudentExamScore, Long> {
    List<StudentExamScore> findByExamUploadId(Long examUploadId);

    List<StudentExamScore> findByStudentId(Long studentId);
}
