package com.tulip.host.repository;

import com.tulip.host.domain.StudentBehaviourScore;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentBehaviourScoreRepository extends JpaRepository<StudentBehaviourScore, Long> {
    List<StudentBehaviourScore> findByAcademicUploadId(Long academicUploadId);

    @Query(
        "SELECT s FROM StudentBehaviourScore s " +
        "JOIN FETCH s.academicUpload a " +
        "JOIN FETCH s.assessmentParameter p " +
        "WHERE s.student.id = :studentId " +
        "ORDER BY a.weekStartDate DESC"
    )
    List<StudentBehaviourScore> findByStudentIdWithDetails(@Param("studentId") Long studentId);
}
