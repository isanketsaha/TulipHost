package com.tulip.host.service;

import static com.tulip.host.config.Constants.DD_MM_YYYY_FORMATTER;

import com.tulip.host.client.FeignErrorDecoder;
import com.tulip.host.client.eOffice;
import com.tulip.host.data.AttendanceResponseDTO;
import com.tulip.host.data.AttendanceSummaryDTO;
import com.tulip.host.data.EofficeAttendanceDTO;
import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.enums.TypeEnum;
import com.tulip.host.repository.AcademicCalendarRepository;
import com.tulip.host.repository.EmployeeLeaveRepository;
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
        // Calculate total working days (Monday to Friday)
        int totalWorkingDays = calculateWorkingDays(startDate, endDate);

        LOG.debug("Total working days in period ({} to {}): {}", startDate, endDate, totalWorkingDays);
        List<AcademicCalendar> holiday = academicCalendarRepository
            .findByDateRange(startDate, endDate)
            .stream()
            .filter(event -> event.getEventType().equals(TypeEnum.HOLIDAY))
            .toList();

        // Group by employee code
        Map<String, List<EofficeAttendanceDTO>> byEmployee = attendanceRecords
            .stream()
            .collect(Collectors.groupingBy(EofficeAttendanceDTO::getEmpcode));

        Map<String, AttendanceSummaryDTO> summary = new HashMap<>();

        for (Map.Entry<String, List<EofficeAttendanceDTO>> entry : byEmployee.entrySet()) {
            String empcode = entry.getKey();
            List<EmployeeLeave> leaveBalanceByTid = employeeLeaveRepository.findLeaveBalanceByTid(empcode, startDate, endDate);
            List<EofficeAttendanceDTO> records = entry.getValue();

            // Get employee name from first record
            String empName = records.get(0).getName();

            // Separate present dates
            Set<String> presentDates = new TreeSet<>(Comparator.comparing(s -> LocalDate.parse(s, DATE_FORMATTER)));

            for (EofficeAttendanceDTO record : records) {
                String status = record.getStatus();
                String dateStr = record.getDateString();

                // Only "P" status is considered present
                if ("P".equalsIgnoreCase(status)) {
                    presentDates.add(dateStr);
                }
            }

            // Get all applied leave dates for this employee
            Set<String> appliedLeaveDates = getAppliedLeaveDates(leaveBalanceByTid);

            LOG.debug("Employee {} has {} days of applied leave", empcode, appliedLeaveDates.size());

            // Get all working days in the period
            Set<String> allWorkingDays = getAllWorkingDaysInPeriod(startDate, endDate);

            // Absent days = all working days minus present days minus applied leave days (excludes holidays)
            Set<String> absentDates = new TreeSet<>(Comparator.comparing(s -> LocalDate.parse(s, DATE_FORMATTER)));
            for (String workingDay : allWorkingDays) {
                LocalDate date = LocalDate.parse(workingDay, DATE_FORMATTER);
                boolean isPresent = presentDates.contains(workingDay);
                boolean isOnLeave = appliedLeaveDates.contains(workingDay);
                boolean isHoliday = isDateWithinHoliday(date, holiday);

                // Mark as absent only if: not present AND not on leave AND not a holiday
                if (!isPresent && !isOnLeave && !isHoliday) {
                    absentDates.add(workingDay);
                }
            }

            int presentCount = presentDates.size();
            int absentCount = absentDates.size();
            int appliedLeaveCount = appliedLeaveDates.size();

            LOG.debug(
                "Employee {} - Present: {} days, Absent: {} days, Applied Leave: {} days out of {} working days",
                empcode,
                presentCount,
                absentCount,
                appliedLeaveCount,
                totalWorkingDays
            );

            summary.put(
                empcode,
                AttendanceSummaryDTO.builder()
                    .empName(empName)
                    .totalWorkingDays(totalWorkingDays)
                    .presentDays(presentCount)
                    .absentDays(absentCount)
                    .appliedLeaveDays(appliedLeaveCount)
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
