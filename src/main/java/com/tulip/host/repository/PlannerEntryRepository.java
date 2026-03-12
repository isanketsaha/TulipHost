package com.tulip.host.repository;

import com.tulip.host.domain.PlannerEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlannerEntryRepository extends JpaRepository<PlannerEntry, Long> {
    List<PlannerEntry> findByAcademicUploadId(Long academicUploadId);
}
