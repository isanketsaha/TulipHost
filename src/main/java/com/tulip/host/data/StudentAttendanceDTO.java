package com.tulip.host.data;

import com.tulip.host.enums.AttendanceStatus;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAttendanceDTO {

    private Long id;
    private Long studentId;
    private LocalDate date;
    private int numberOfDays;
    private AttendanceStatus status;
    private String remarks;
}
