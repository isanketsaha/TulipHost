package com.tulip.host.repository;

import com.tulip.host.domain.ClassroomAcademicUpload;
import com.tulip.host.enums.AcademicUploadType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomAcademicUploadRepository extends JpaRepository<ClassroomAcademicUpload, Long> {
    Optional<ClassroomAcademicUpload> findByClassDetailIdAndSubjectKeyAndWeekStartDateAndUploadType(
        Long classDetailId,
        String subjectKey,
        LocalDate weekStartDate,
        AcademicUploadType uploadType
    );

    List<ClassroomAcademicUpload> findByClassDetailIdAndUploadTypeOrderByWeekStartDateDesc(
        Long classDetailId,
        AcademicUploadType uploadType
    );
}
