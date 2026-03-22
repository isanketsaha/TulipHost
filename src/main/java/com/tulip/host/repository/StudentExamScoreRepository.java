package com.tulip.host.repository;

import com.tulip.host.domain.StudentExamScore;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentExamScoreRepository extends JpaRepository<StudentExamScore, Long> {
    List<StudentExamScore> findByExamUploadId(Long examUploadId);

    List<StudentExamScore> findByStudentId(Long studentId);

    @Query("SELECT s FROM StudentExamScore s " + "JOIN FETCH s.student " + "WHERE s.examUpload.classDetail.id = :classroomId")
    List<StudentExamScore> findAllByClassroomId(@Param("classroomId") Long classroomId);
}
