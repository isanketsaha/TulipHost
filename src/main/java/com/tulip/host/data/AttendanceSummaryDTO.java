package com.tulip.host.data;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to hold attendance summary for an employee.
 * Groups attendance data by employee code with present days and absent days.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSummaryDTO {

    private String empName;
    private Integer totalWorkingDays;
    private Integer presentDays;
    private Integer absentDays;
    private Integer appliedLeaveDays;
    private Set<String> presentDates;
    private Set<String> absentDates;
    private Set<String> leaveApplied;
}
