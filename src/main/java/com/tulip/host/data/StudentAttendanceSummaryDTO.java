package com.tulip.host.data;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAttendanceSummaryDTO {

    private int percent;
    private int present;
    private int absent;
    private int leave;
    private int totalDays;

    private List<MonthlyAttendance> monthlyBreakdown;
    private List<WeeklyAttendance> weeklyBreakdown;

    @Data
    @Builder
    public static class MonthlyAttendance {

        private String month; // e.g. "Jan 2025"
        private int percent;
    }

    @Data
    @Builder
    public static class WeeklyAttendance {

        private String weekStart; // e.g. "03/03/2025"
        private String mon;
        private String tue;
        private String wed;
        private String thu;
        private String fri;
        private int percent;
    }
}
