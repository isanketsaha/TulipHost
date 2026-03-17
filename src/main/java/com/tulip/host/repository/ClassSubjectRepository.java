package com.tulip.host.repository;

import com.tulip.host.domain.ClassSubject;
import com.tulip.host.enums.ExamType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassSubjectRepository extends JpaRepository<ClassSubject, Long> {
    List<ClassSubject> findAllByClassDetailId(Long classDetailId);

    Optional<ClassSubject> findByClassDetailIdAndSubjectKey(Long classDetailId, String subjectKey);

    @Query(
        "SELECT cs FROM ClassSubject cs WHERE cs.classDetail.id = :classDetailId AND :examType MEMBER OF cs.examTypes AND cs.included = true"
    )
    List<ClassSubject> findByClassDetailIdAndExamType(@Param("classDetailId") Long classDetailId, @Param("examType") ExamType examType);
}
