package com.tulip.host.repository;

import com.tulip.host.domain.StudentAttendance;
import com.tulip.host.enums.AttendanceStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {
    // All exception records for a student (used to compute attendance summary)
    List<StudentAttendance> findByStudentIdOrderByDateDesc(Long studentId);

    // Filtered by status — used to count absent vs leave separately
    List<StudentAttendance> findByStudentIdAndStatus(Long studentId, AttendanceStatus status);

    // Date-range query for monthly breakdowns
    List<StudentAttendance> findByStudentIdAndDateBetween(Long studentId, LocalDate from, LocalDate to);

    // All exception rows for a given weekly upload (for classroom detail view)
    List<StudentAttendance> findByClassroomAttendanceId(Long classroomAttendanceId);

    // Overlap check for leave validation: find any LEAVE row for a student whose range intersects the given range
    List<StudentAttendance> findByStudentIdAndStatusAndDateBetween(Long studentId, AttendanceStatus status, LocalDate from, LocalDate to);

    // Pre-approved leaves for a set of students whose range overlaps a given week.
    // leave overlaps week if: leave.date <= weekEnd AND (leave.date + leave.numberOfDays - 1) >= weekStart
    // Fetch leaves starting on or before weekEnd, then filter in-service for end overlap.
    List<StudentAttendance> findByStudentIdInAndStatusAndDateLessThanEqual(
        List<Long> studentIds,
        AttendanceStatus status,
        LocalDate weekEnd
    );
}
