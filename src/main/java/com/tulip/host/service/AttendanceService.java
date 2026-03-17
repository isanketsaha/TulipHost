package com.tulip.host.service;

import com.tulip.host.data.ClassroomAttendanceDTO;
import com.tulip.host.data.StudentAttendanceDTO;
import com.tulip.host.data.StudentAttendanceSummaryDTO;
import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.ClassroomAttendance;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.StudentAttendance;
import com.tulip.host.domain.UploadRecord;
import com.tulip.host.enums.AttendanceStatus;
import com.tulip.host.repository.AcademicCalendarRepository;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.ClassroomAttendanceRepository;
import com.tulip.host.repository.StudentAttendanceRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.UploadRecordRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private static final String[] WEEK_DAYS = { "Mon", "Tue", "Wed", "Thu", "Fri" };
    private static final DateTimeFormatter DATE_HEADER = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DATE_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MONTH_DISPLAY = DateTimeFormatter.ofPattern("MMM yyyy");

    private final ClassDetailRepository classDetailRepository;
    private final StudentRepository studentRepository;
    private final ClassroomAttendanceRepository classroomAttendanceRepository;
    private final StudentAttendanceRepository studentAttendanceRepository;
    private final AcademicCalendarRepository academicCalendarRepository;
    private final UploadService uploadService;
    private final UploadRecordRepository uploadRecordRepository;

    // ─────────────────────────────────────────────────────────────
    // TEMPLATE GENERATION
    // ─────────────────────────────────────────────────────────────

    /**
     * Generates a pre-filled Excel attendance template for the given classroom and week.
     * Holiday columns are marked and locked; pre-approved leave cells are pre-filled with "L".
     */
    @Transactional
    public byte[] generateTemplate(Long classroomId, LocalDate weekStart) throws IOException {
        LocalDate weekEnd = weekStart.plusDays(4); // Mon to Fri

        ClassDetail classDetail = classDetailRepository.findByClass(classroomId);
        if (classDetail == null) {
            throw new IllegalArgumentException("Classroom not found: " + classroomId);
        }

        // Active students sorted by name
        List<Student> students = classDetail
            .getStudents()
            .stream()
            .filter(s -> Boolean.TRUE.equals(s.getActive()))
            .sorted(Comparator.comparing(s -> s.getName().toUpperCase()))
            .collect(Collectors.toList());

        // Holidays overlapping the week
        List<AcademicCalendar> holidays = academicCalendarRepository.findByDateRange(weekStart, weekEnd);
        Set<LocalDate> holidayDates = holidays
            .stream()
            .flatMap(h -> h.getStartDate().datesUntil(h.getEndDate().plusDays(1)))
            .filter(d -> !d.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !d.getDayOfWeek().equals(DayOfWeek.SUNDAY))
            .collect(Collectors.toSet());

        // Pre-approved leaves for these students overlapping this week
        List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
        // leave.date <= weekEnd; filter in-memory for overlap end: leave.date + numberOfDays - 1 >= weekStart
        List<StudentAttendance> leaves = studentAttendanceRepository
            .findByStudentIdInAndStatusAndDateLessThanEqual(studentIds, AttendanceStatus.LEAVE, weekEnd)
            .stream()
            .filter(l -> l.getDate().plusDays(l.getNumberOfDays() - 1).compareTo(weekStart) >= 0)
            .collect(Collectors.toList());

        // Build lookup: studentId → set of leave dates within this week
        Map<Long, Set<LocalDate>> leavesByStudent = new HashMap<>();
        for (StudentAttendance leave : leaves) {
            LocalDate leaveEnd = leave.getDate().plusDays(leave.getNumberOfDays() - 1);
            Set<LocalDate> leaveDates = leave
                .getDate()
                .datesUntil(leaveEnd.plusDays(1))
                .filter(d -> !d.isBefore(weekStart) && !d.isAfter(weekEnd))
                .collect(Collectors.toSet());
            leavesByStudent.computeIfAbsent(leave.getStudent().getId(), k -> new java.util.HashSet<>()).addAll(leaveDates);
        }

        return buildExcel(students, weekStart, weekEnd, holidayDates, leavesByStudent);
    }

    private byte[] buildExcel(
        List<Student> students,
        LocalDate weekStart,
        LocalDate weekEnd,
        Set<LocalDate> holidayDates,
        Map<Long, Set<LocalDate>> leavesByStudent
    ) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Attendance");

            // ── Styles ──
            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);

            CellStyle holidayStyle = workbook.createCellStyle();
            holidayStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            holidayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            holidayStyle.setAlignment(HorizontalAlignment.CENTER);
            holidayStyle.setLocked(true);

            CellStyle leaveStyle = workbook.createCellStyle();
            leaveStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            leaveStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            leaveStyle.setAlignment(HorizontalAlignment.CENTER);
            leaveStyle.setLocked(true);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            // ── Header row ──
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Student Name");
            header.getCell(0).setCellStyle(headerStyle);
            header.createCell(1).setCellValue("Student ID");
            header.getCell(1).setCellStyle(headerStyle);

            List<LocalDate> weekDates = weekStart.datesUntil(weekEnd.plusDays(1)).collect(Collectors.toList());
            for (int i = 0; i < weekDates.size(); i++) {
                LocalDate day = weekDates.get(i);
                String dayLabel = WEEK_DAYS[i] + " " + day.format(DATE_HEADER);
                Cell cell = header.createCell(i + 2);
                cell.setCellValue(holidayDates.contains(day) ? dayLabel + " (H)" : dayLabel);
                cell.setCellStyle(headerStyle);
            }

            // ── Data rows ──
            int rowIdx = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(student.getName());
                row.createCell(1).setCellValue(student.getId());

                Set<LocalDate> studentLeaves = leavesByStudent.getOrDefault(student.getId(), Collections.emptySet());

                for (int i = 0; i < weekDates.size(); i++) {
                    LocalDate day = weekDates.get(i);
                    Cell cell = row.createCell(i + 2);
                    if (holidayDates.contains(day)) {
                        cell.setCellValue("H");
                        cell.setCellStyle(holidayStyle);
                    } else if (studentLeaves.contains(day)) {
                        cell.setCellValue("L");
                        cell.setCellStyle(leaveStyle);
                    } else {
                        cell.setCellValue("");
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < weekDates.size() + 2; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UPLOAD & PARSE
    // ─────────────────────────────────────────────────────────────

    /**
     * Saves the Excel file to S3, parses it, and persists ClassroomAttendance + StudentAttendance records.
     * Only absent rows are persisted — present students are NOT stored.
     */
    @Transactional
    public ClassroomAttendanceDTO processUpload(Long classroomId, LocalDate weekStart, MultipartFile file) throws Exception {
        // Duplicate guard
        if (classroomAttendanceRepository.findByClassDetailIdAndWeekStartDate(classroomId, weekStart).isPresent()) {
            throw new IllegalStateException("Attendance already uploaded for this classroom and week.");
        }

        ClassDetail classDetail = classDetailRepository
            .findById(classroomId)
            .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomId));

        LocalDate weekEnd = weekStart.plusDays(4);

        // Save raw file to S3 and persist an UploadRecord for normalization
        String fileUid = uploadService.save(file, "academic/attendance");
        UploadRecord uploadRecord = uploadRecordRepository.save(
            UploadRecord.builder()
                .uid(fileUid)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size((int) file.getSize())
                .status("done")
                .uploadType("attendance")
                .build()
        );

        // Resolve holidays for this week
        List<AcademicCalendar> holidays = academicCalendarRepository.findByDateRange(weekStart, weekEnd);
        Set<LocalDate> holidayDates = holidays
            .stream()
            .flatMap(h -> h.getStartDate().datesUntil(h.getEndDate().plusDays(1)))
            .filter(d -> !d.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !d.getDayOfWeek().equals(DayOfWeek.SUNDAY))
            .collect(Collectors.toSet());

        int totalWorkingDays = (int) weekStart.datesUntil(weekEnd.plusDays(1)).filter(d -> !holidayDates.contains(d)).count();
        int holidayCount = (int) weekStart.datesUntil(weekEnd.plusDays(1)).filter(holidayDates::contains).count();

        // Parse Excel
        List<StudentAttendance> absentRows = parseAttendanceExcel(file, weekStart, weekEnd, classDetail, holidayDates);

        int totalStudents = classDetail
            .getStudents()
            .stream()
            .filter(s -> Boolean.TRUE.equals(s.getActive()))
            .collect(Collectors.toSet())
            .size();
        int absentCount = absentRows.size();

        // Count leave days overlapping this week (already persisted LEAVE rows)
        List<Long> studentIds = classDetail.getStudents().stream().map(Student::getId).collect(Collectors.toList());
        int leaveCount = (int) studentAttendanceRepository
            .findByStudentIdInAndStatusAndDateLessThanEqual(studentIds, AttendanceStatus.LEAVE, weekEnd)
            .stream()
            .filter(l -> l.getDate().plusDays(l.getNumberOfDays() - 1).compareTo(weekStart) >= 0)
            .count();

        int presentCount = (totalStudents * totalWorkingDays) - absentCount - leaveCount;

        // Persist Level 1
        ClassroomAttendance classroomAttendance = ClassroomAttendance.builder()
            .classDetail(classDetail)
            .weekStartDate(weekStart)
            .totalStudents(totalStudents)
            .presentCount(Math.max(0, presentCount))
            .absentCount(absentCount)
            .leaveCount(leaveCount)
            .totalWorkingDays(totalWorkingDays)
            .holidayCount(holidayCount)
            .s3Upload(uploadRecord)
            .build();
        classroomAttendance = classroomAttendanceRepository.save(classroomAttendance);

        // Link absent rows to the Level 1 record and batch save Level 2
        final ClassroomAttendance savedParent = classroomAttendance;
        absentRows.forEach(r -> r.setClassroomAttendance(savedParent));
        studentAttendanceRepository.saveAll(absentRows);

        return toDTO(savedParent);
    }

    private List<StudentAttendance> parseAttendanceExcel(
        MultipartFile file,
        LocalDate weekStart,
        LocalDate weekEnd,
        ClassDetail classDetail,
        Set<LocalDate> holidayDates
    ) throws IOException {
        List<LocalDate> weekDates = weekStart.datesUntil(weekEnd.plusDays(1)).collect(Collectors.toList());

        // Build student lookup by ID
        Map<Long, Student> studentMap = classDetail
            .getStudents()
            .stream()
            .filter(s -> Boolean.TRUE.equals(s.getActive()))
            .collect(Collectors.toMap(Student::getId, s -> s));

        List<StudentAttendance> result = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()))) {
            Sheet sheet = workbook.getSheetAt(0);
            // Row 0 is the header — skip it. Data starts at row 1.
            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;

                Cell idCell = row.getCell(1);
                if (idCell == null) continue;
                long studentId = (long) idCell.getNumericCellValue();

                Student student = studentMap.get(studentId);
                if (student == null) {
                    log.warn("Student ID {} from template not found in classroom {}", studentId, classDetail.getId());
                    continue;
                }

                for (int dayIdx = 0; dayIdx < weekDates.size(); dayIdx++) {
                    LocalDate day = weekDates.get(dayIdx);
                    if (holidayDates.contains(day)) continue; // skip holidays

                    Cell cell = row.getCell(dayIdx + 2);
                    if (cell == null) continue;

                    String value = cell.getStringCellValue().trim().toUpperCase();
                    if ("A".equals(value)) {
                        result.add(
                            StudentAttendance.builder().student(student).date(day).numberOfDays(1).status(AttendanceStatus.ABSENT).build()
                        );
                    }
                    // "L" cells are pre-filled locks — leave records already exist, skip
                }
            }
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // LEAVE SUBMISSION
    // ─────────────────────────────────────────────────────────────

    /**
     * Records a pre-approved multi-day leave for a student.
     * Not linked to any classroom upload (classroomAttendance = null).
     */
    @Transactional
    public StudentAttendanceDTO submitLeave(Long studentId, LocalDate startDate, int numberOfDays, String remarks) {
        Student student = studentRepository
            .findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        LocalDate endDate = startDate.plusDays(numberOfDays - 1);

        // Overlap check: no existing leave or absent record in this date range
        List<StudentAttendance> existing = studentAttendanceRepository.findByStudentIdAndDateBetween(studentId, startDate, endDate);
        if (!existing.isEmpty()) {
            throw new IllegalStateException("An attendance exception already exists for this student in the given date range.");
        }

        StudentAttendance leave = StudentAttendance.builder()
            .student(student)
            .date(startDate)
            .numberOfDays(numberOfDays)
            .status(AttendanceStatus.LEAVE)
            .remarks(remarks)
            .build();

        leave = studentAttendanceRepository.save(leave);
        return toDTO(leave);
    }

    // ─────────────────────────────────────────────────────────────
    // STUDENT ATTENDANCE SUMMARY
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns attendance summary for a student — overall %, monthly and weekly breakdowns.
     * Present students are derived (totalWorkingDays - exceptions), never stored directly.
     */
    @Transactional(readOnly = true)
    public StudentAttendanceSummaryDTO getStudentAttendanceSummary(Long studentId) {
        // All ClassroomAttendance records for the student's current class (to get working days)
        Student student = studentRepository
            .findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        // Get the most recent class for the student
        ClassDetail currentClass = student.getClassDetails().isEmpty() ? null : student.getClassDetails().first();

        int totalWorkingDays = 0;
        Map<String, int[]> monthlyWorkingDays = new HashMap<>(); // month → [workingDays, exceptionDays]
        List<ClassroomAttendance> uploads = Collections.emptyList();

        if (currentClass != null) {
            uploads = classroomAttendanceRepository.findByClassDetailIdOrderByWeekStartDateDesc(currentClass.getId());
            for (ClassroomAttendance upload : uploads) {
                totalWorkingDays += upload.getTotalWorkingDays();
                String month = upload.getWeekStartDate().format(MONTH_DISPLAY);
                monthlyWorkingDays.computeIfAbsent(month, k -> new int[] { 0, 0 })[0] += upload.getTotalWorkingDays();
            }
        }

        // All exception rows for this student
        List<StudentAttendance> exceptions = studentAttendanceRepository.findByStudentIdOrderByDateDesc(studentId);

        int totalAbsent = (int) exceptions
            .stream()
            .filter(e -> e.getStatus() == AttendanceStatus.ABSENT)
            .mapToInt(StudentAttendance::getNumberOfDays)
            .sum();
        int totalLeave = (int) exceptions
            .stream()
            .filter(e -> e.getStatus() == AttendanceStatus.LEAVE)
            .mapToInt(StudentAttendance::getNumberOfDays)
            .sum();
        int totalExceptions = totalAbsent + totalLeave;
        int present = Math.max(0, totalWorkingDays - totalExceptions);
        int percent = totalWorkingDays == 0 ? 0 : ((present * 100) / totalWorkingDays);

        // Monthly breakdown
        for (StudentAttendance ex : exceptions) {
            String month = ex.getDate().format(MONTH_DISPLAY);
            if (monthlyWorkingDays.containsKey(month)) {
                monthlyWorkingDays.get(month)[1] += ex.getNumberOfDays();
            }
        }
        List<StudentAttendanceSummaryDTO.MonthlyAttendance> monthly = monthlyWorkingDays
            .entrySet()
            .stream()
            .map(e -> {
                int wd = e.getValue()[0];
                int exc = e.getValue()[1];
                int p = wd == 0 ? 0 : ((Math.max(0, wd - exc) * 100) / wd);
                return StudentAttendanceSummaryDTO.MonthlyAttendance.builder().month(e.getKey()).percent(p).build();
            })
            .sorted(Comparator.comparing(StudentAttendanceSummaryDTO.MonthlyAttendance::getMonth))
            .collect(Collectors.toList());

        // Weekly breakdown — build per-day absent/leave map
        Map<LocalDate, AttendanceStatus> exceptionByDate = new HashMap<>();
        for (StudentAttendance ex : exceptions) {
            if (ex.getStatus() == AttendanceStatus.ABSENT) {
                exceptionByDate.put(ex.getDate(), AttendanceStatus.ABSENT);
            } else {
                // LEAVE: mark each individual day within the leave range
                LocalDate leaveEnd = ex.getDate().plusDays(ex.getNumberOfDays() - 1);
                ex.getDate().datesUntil(leaveEnd.plusDays(1)).forEach(d -> exceptionByDate.put(d, AttendanceStatus.LEAVE));
            }
        }

        List<StudentAttendanceSummaryDTO.WeeklyAttendance> weekly = uploads
            .stream()
            .map(upload -> buildWeeklyAttendance(upload, exceptionByDate))
            .collect(Collectors.toList());

        return StudentAttendanceSummaryDTO.builder()
            .percent(percent)
            .present(present)
            .absent(totalAbsent)
            .leave(totalLeave)
            .totalDays(totalWorkingDays)
            .monthlyBreakdown(monthly)
            .weeklyBreakdown(weekly)
            .build();
    }

    private StudentAttendanceSummaryDTO.WeeklyAttendance buildWeeklyAttendance(
        ClassroomAttendance upload,
        Map<LocalDate, AttendanceStatus> exceptionByDate
    ) {
        LocalDate weekStart = upload.getWeekStartDate();
        List<LocalDate> days = weekStart.datesUntil(weekStart.plusDays(5)).collect(Collectors.toList());

        String[] cells = new String[5];
        for (int i = 0; i < 5; i++) {
            LocalDate day = days.get(i);
            AttendanceStatus status = exceptionByDate.get(day);
            cells[i] = status == null ? "P" : status.name().substring(0, 1); // "P", "A", or "L"
        }

        int weeklyExceptions = (int) days.stream().filter(d -> exceptionByDate.containsKey(d)).count();
        int weeklyPercent = upload.getTotalWorkingDays() == 0
            ? 0
            : ((Math.max(0, upload.getTotalWorkingDays() - weeklyExceptions) * 100) / upload.getTotalWorkingDays());

        return StudentAttendanceSummaryDTO.WeeklyAttendance.builder()
            .weekStart(weekStart.format(DATE_DISPLAY))
            .mon(cells[0])
            .tue(cells[1])
            .wed(cells[2])
            .thu(cells[3])
            .fri(cells[4])
            .percent(weeklyPercent)
            .build();
    }

    // ─────────────────────────────────────────────────────────────
    // CLASSROOM HISTORY
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ClassroomAttendanceDTO> getClassroomAttendanceHistory(Long classroomId) {
        return classroomAttendanceRepository
            .findByClassDetailIdOrderByWeekStartDateDesc(classroomId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // MAPPERS
    // ─────────────────────────────────────────────────────────────

    private ClassroomAttendanceDTO toDTO(ClassroomAttendance entity) {
        return ClassroomAttendanceDTO.builder()
            .id(entity.getId())
            .classroomId(entity.getClassDetail().getId())
            .weekStartDate(entity.getWeekStartDate())
            .totalStudents(entity.getTotalStudents())
            .presentCount(entity.getPresentCount())
            .absentCount(entity.getAbsentCount())
            .leaveCount(entity.getLeaveCount())
            .totalWorkingDays(entity.getTotalWorkingDays())
            .holidayCount(entity.getHolidayCount())
            .fileUid(entity.getS3Upload() != null ? entity.getS3Upload().getUid() : null)
            .uploadedBy(entity.getCreatedBy())
            .createdDate(entity.getCreatedDate() != null ? entity.getCreatedDate().toString() : null)
            .build();
    }

    private StudentAttendanceDTO toDTO(StudentAttendance entity) {
        return StudentAttendanceDTO.builder()
            .id(entity.getId())
            .studentId(entity.getStudent().getId())
            .date(entity.getDate())
            .numberOfDays(entity.getNumberOfDays())
            .status(entity.getStatus())
            .remarks(entity.getRemarks())
            .build();
    }
}
