package com.tulip.host.service;

import static com.tulip.host.config.Constants.DD_MM_YYYY_FORMATTER;

import com.tulip.host.client.eOffice;
import com.tulip.host.data.AttendanceResponseDTO;
import com.tulip.host.data.AttendanceSummaryDTO;
import com.tulip.host.data.EofficeAttendanceDTO;
import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.enums.TypeEnum;
import com.tulip.host.repository.AcademicCalendarRepository;
import com.tulip.host.repository.EmployeeLeaveRepository;
import com.tulip.host.repository.EmployeeRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for calling external APIs using Feign clients.
 * Each client automatically includes its service-specific auth token.
 */
@Service
@RequiredArgsConstructor
public class eOfficeApiService {

    private static final Logger LOG = LoggerFactory.getLogger(eOfficeApiService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final eOffice eOfficeClient;
    private final AcademicCalendarRepository academicCalendarRepository;
    private final EmployeeLeaveRepository employeeLeaveRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Fetch raw in/out punch data from eOffice service. Dates must be in dd/MM/yyyy format.
     */
    public AttendanceResponseDTO getInOutPunchData(String empcode, String fromDate, String toDate) {
        LOG.debug("Fetching raw punch data from eOffice - Empcode: {}, FromDate: {}, ToDate: {}", empcode, fromDate, toDate);
        return eOfficeClient.downloadInOutPunchData(empcode, fromDate, toDate);
    }

    /**
     * Fetch timesheet data from eOffice service and group by employee.
     * Groups all attendance records by employee code and calculates present/absent days.
     *
     * @param emp       Employee code (e.g., "ALL")
     * @param startDate Start date
     * @param endDate   End date
     * @return Map with employee code as key and AttendanceSummaryDTO as value
     */
    public Map<String, AttendanceSummaryDTO> getTimesheetSummary(String emp, LocalDate startDate, LocalDate endDate) {
        try {
            String formattedStartDate = startDate.format(DD_MM_YYYY_FORMATTER);
            String formattedEndDate = endDate.format(DD_MM_YYYY_FORMATTER);

            LOG.debug(
                "Fetching timesheet from eOffice API - Empcode: {}, FromDate: {}, ToDate: {}",
                emp,
                formattedStartDate,
                formattedEndDate
            );

            AttendanceResponseDTO response = eOfficeClient.downloadInOutPunchData(emp, formattedStartDate, formattedEndDate);

            if (response == null || response.getInOutPunchData() == null || response.getInOutPunchData().isEmpty()) {
                LOG.warn("No attendance records found for the given period");
                return new HashMap<>();
            }

            return groupAndSummarizeAttendance(response.getInOutPunchData(), startDate, endDate);
        } catch (Exception e) {
            LOG.error("Unexpected error while calling eOffice service", e);
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * Group attendance records by employee code and calculate present/absent days.
     * Working days are Monday to Friday.
     *
     * @param attendanceRecords List of attendance records from eOffice
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return Map with employee code as key and attendance summary as value
     */
    private Map<String, AttendanceSummaryDTO> groupAndSummarizeAttendance(
        List<EofficeAttendanceDTO> attendanceRecords,
        LocalDate startDate,
        LocalDate endDate
    ) {
        // Total Mon-Fri working days in period (baseline for scaling)
        int baseWorkingDays = calculateWorkingDays(startDate, endDate);
        // Number of full/partial ISO weeks in period (used to scale part-time employee quotas)
        int weeksInPeriod = (int) Math.ceil(baseWorkingDays / 5.0);

        LOG.debug("Base working days ({} to {}): {}, weeks: {}", startDate, endDate, baseWorkingDays, weeksInPeriod);

        List<AcademicCalendar> holiday = academicCalendarRepository
            .findByDateRange(startDate, endDate)
            .stream()
            .filter(event -> event.getEventType().equals(TypeEnum.HOLIDAY))
            .toList();

        // Group by employee code
        Map<String, List<EofficeAttendanceDTO>> byEmployee = attendanceRecords
            .stream()
            .collect(Collectors.groupingBy(EofficeAttendanceDTO::getEmpcode));

        // Fetch working-days-per-week config for all employees in one query
        Map<String, Integer> workingDaysConfig = employeeRepository
            .findByTidIn(byEmployee.keySet())
            .stream()
            .filter(e -> e.getTid() != null && e.getWorkingDaysInWeek() != null)
            .collect(Collectors.toMap(Employee::getTid, Employee::getWorkingDaysInWeek));

        // Get all Mon-Fri working days in the period (shared across employees)
        Set<String> allWorkingDays = getAllWorkingDaysInPeriod(startDate, endDate);

        Map<String, AttendanceSummaryDTO> summary = new HashMap<>();

        for (Map.Entry<String, List<EofficeAttendanceDTO>> entry : byEmployee.entrySet()) {
            String empcode = entry.getKey();
            List<EmployeeLeave> leaveBalanceByTid = employeeLeaveRepository.findLeaveBalanceByTid(empcode, startDate, endDate);
            List<EofficeAttendanceDTO> records = entry.getValue();

            String empName = records.get(0).getName();

            // Collect present dates (only "P" status)
            Set<String> presentDates = new TreeSet<>(Comparator.comparing(s -> LocalDate.parse(s, DATE_FORMATTER)));
            for (EofficeAttendanceDTO record : records) {
                if ("P".equalsIgnoreCase(record.getStatus())) {
                    presentDates.add(record.getDateString());
                }
            }

            Set<String> appliedLeaveDates = getAppliedLeaveDates(leaveBalanceByTid);

            // Absent = working days not present, not on leave, not a holiday
            Set<String> absentDates = new TreeSet<>(Comparator.comparing(s -> LocalDate.parse(s, DATE_FORMATTER)));
            for (String workingDay : allWorkingDays) {
                LocalDate date = LocalDate.parse(workingDay, DATE_FORMATTER);
                if (!presentDates.contains(workingDay) && !appliedLeaveDates.contains(workingDay) && !isDateWithinHoliday(date, holiday)) {
                    absentDates.add(workingDay);
                }
            }

            // Per-employee total working days: scale their weekly quota to the period length
            Integer configuredDaysPerWeek = workingDaysConfig.get(empcode);
            int effectiveTotalWorkingDays;
            if (configuredDaysPerWeek != null) {
                // Cap at baseWorkingDays so we never exceed actual Mon-Fri days in period
                effectiveTotalWorkingDays = Math.min(configuredDaysPerWeek * weeksInPeriod, baseWorkingDays);
            } else {
                effectiveTotalWorkingDays = baseWorkingDays;
            }

            // Cap absent days for part-time employees: quota - present - leave = max absent allowed
            int presentCount = presentDates.size();
            int appliedLeaveCount = appliedLeaveDates.size();
            int maxAbsent = Math.max(0, effectiveTotalWorkingDays - presentCount - appliedLeaveCount);
            int absentCount = Math.min(absentDates.size(), maxAbsent);

            // For part-time employees, also trim the absentDates set so the email/report only
            // shows the dates that actually count (last N dates — same logic as the UI).
            if (configuredDaysPerWeek != null && absentDates.size() > absentCount) {
                List<String> allAbsent = new java.util.ArrayList<>(absentDates);
                // absentDates is sorted ascending; take the last `absentCount` entries
                absentDates = allAbsent
                    .subList(allAbsent.size() - absentCount, allAbsent.size())
                    .stream()
                    .collect(
                        java.util.stream.Collectors.toCollection(() ->
                            new TreeSet<>(Comparator.comparing(s -> LocalDate.parse(s, DATE_FORMATTER)))
                        )
                    );
            }

            LOG.debug(
                "Employee {} (quota={}/week) - Present: {}, Absent: {}, Leave: {} out of {} effective working days",
                empcode,
                configuredDaysPerWeek != null ? configuredDaysPerWeek : "all",
                presentCount,
                absentCount,
                appliedLeaveCount,
                effectiveTotalWorkingDays
            );

            summary.put(
                empcode,
                AttendanceSummaryDTO.builder()
                    .empName(empName)
                    .totalWorkingDays(effectiveTotalWorkingDays)
                    .presentDays(presentCount)
                    .absentDays(absentCount)
                    .appliedLeaveDays(appliedLeaveCount)
                    .workingDaysInWeek(configuredDaysPerWeek)
                    .presentDates(presentDates)
                    .absentDates(absentDates)
                    .leaveApplied(appliedLeaveDates)
                    .build()
            );
        }

        return summary;
    }

    /**
     * Get all working days (Monday to Friday) in a date range as formatted strings.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Set of working days in dd/MM/yyyy format
     */
    private Set<String> getAllWorkingDaysInPeriod(LocalDate startDate, LocalDate endDate) {
        Set<String> workingDays = new TreeSet<>(Comparator.comparing(s -> LocalDate.parse(s, DATE_FORMATTER)));
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            int dayOfWeek = current.getDayOfWeek().getValue();
            // Monday = 1, Friday = 5
            if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                workingDays.add(current.format(DATE_FORMATTER));
            }
            current = current.plusDays(1);
        }

        return workingDays;
    }

    /**
     * Calculate the number of working days (Monday to Friday) between two dates.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Number of working days
     */
    private int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        int workingDays = 0;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            int dayOfWeek = current.getDayOfWeek().getValue();
            // Monday = 1, Friday = 5
            if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                workingDays++;
            }
            current = current.plusDays(1);
        }

        return workingDays;
    }

    /**
     * Check if a date falls within any holiday period.
     *
     * @param date The date to check
     * @param holidays List of academic calendar holidays
     * @return true if date is within a holiday range, false otherwise
     */
    private boolean isDateWithinHoliday(LocalDate date, List<AcademicCalendar> holidays) {
        return holidays.stream().anyMatch(event -> !date.isBefore(event.getStartDate()) && !date.isAfter(event.getEndDate()));
    }

    /**
     * Extract all leave dates from employee leave records.
     * Collects all dates between leave start and end dates.
     *
     * @param leaveRecords List of employee leave records
     * @return Set of leave dates in dd/MM/yyyy format
     */
    private Set<String> getAppliedLeaveDates(List<EmployeeLeave> leaveRecords) {
        Set<String> leaveDates = new TreeSet<>(Comparator.comparing(s -> LocalDate.parse(s, DATE_FORMATTER)));

        if (leaveRecords == null || leaveRecords.isEmpty()) {
            return leaveDates;
        }

        for (EmployeeLeave leave : leaveRecords) {
            LocalDate current = leave.getStartDate();
            LocalDate endDate = leave.getEndDate();

            // Add all dates from start to end date
            while (!current.isAfter(endDate)) {
                // Only include working days (Monday to Friday)
                int dayOfWeek = current.getDayOfWeek().getValue();
                if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                    leaveDates.add(current.format(DATE_FORMATTER));
                }
                current = current.plusDays(1);
            }
        }

        return leaveDates;
    }
}
