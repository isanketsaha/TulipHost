package com.tulip.host.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "classroom_attendance")
public class ClassroomAttendance extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_detail_id", nullable = false)
    private ClassDetail classDetail;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "total_students", nullable = false)
    private int totalStudents;

    @Column(name = "present_count", nullable = false)
    private int presentCount;

    @Column(name = "absent_count", nullable = false)
    private int absentCount;

    @Column(name = "leave_count", nullable = false)
    private int leaveCount;

    @Column(name = "total_working_days", nullable = false)
    private int totalWorkingDays;

    @Column(name = "holiday_count", nullable = false)
    private int holidayCount;

    // S3 reference for the uploaded Excel file
    @Column(name = "file_uid")
    private String fileUid;
}
