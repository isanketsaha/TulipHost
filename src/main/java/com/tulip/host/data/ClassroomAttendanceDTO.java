package com.tulip.host.data;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassroomAttendanceDTO {

    private Long id;
    private Long classroomId;
    private LocalDate weekStartDate;
    private int totalStudents;
    private int presentCount;
    private int absentCount;
    private int leaveCount;
    private int totalWorkingDays;
    private int holidayCount;
    private String fileUid;
    private String uploadedBy;
    private String createdDate;
}
