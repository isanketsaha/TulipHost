package com.tulip.host.domain;

import com.tulip.host.enums.AttendanceStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "student_attendance")
public class StudentAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Null for LEAVE rows (pre-approved, not tied to a weekly upload)
    // Set for ABSENT rows (recorded during teacher's weekly upload)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_attendance_id")
    private ClassroomAttendance classroomAttendance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // For ABSENT: the specific absent date. For LEAVE: the leave start date.
    @Column(name = "date", nullable = false)
    private LocalDate date;

    // Always 1 for ABSENT. N for multi-day LEAVE.
    @Column(name = "number_of_days", nullable = false)
    private int numberOfDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private AttendanceStatus status;

    @Column(name = "remarks")
    private String remarks;
}
