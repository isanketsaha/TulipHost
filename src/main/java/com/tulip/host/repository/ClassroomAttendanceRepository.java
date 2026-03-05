package com.tulip.host.repository;

import com.tulip.host.domain.ClassroomAttendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomAttendanceRepository extends JpaRepository<ClassroomAttendance, Long> {
    // Used to guard against duplicate uploads for the same classroom + week
    Optional<ClassroomAttendance> findByClassDetailIdAndWeekStartDate(Long classDetailId, LocalDate weekStartDate);

    // Classroom history view — newest first
    List<ClassroomAttendance> findByClassDetailIdOrderByWeekStartDateDesc(Long classDetailId);
}
