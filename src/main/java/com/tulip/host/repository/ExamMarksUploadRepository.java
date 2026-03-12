package com.tulip.host.repository;

import com.tulip.host.domain.ExamMarksUpload;
import com.tulip.host.enums.ExamType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamMarksUploadRepository extends JpaRepository<ExamMarksUpload, Long> {
    Optional<ExamMarksUpload> findByClassDetailIdAndExamTypeAndExamName(Long classDetailId, ExamType examType, String examName);

    List<ExamMarksUpload> findByClassDetailId(Long classDetailId);
}
