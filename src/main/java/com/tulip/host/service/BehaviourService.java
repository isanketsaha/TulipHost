package com.tulip.host.service;

import com.tulip.host.data.AcademicUploadDTO;
import com.tulip.host.data.ClassroomInsightsDTO.BehaviourStudentInsightDTO;
import com.tulip.host.data.ClassroomInsightsDTO.BehaviourSubjectInsightDTO;
import com.tulip.host.data.StudentBehaviourReviewDTO;
import com.tulip.host.data.StudentBehaviourReviewDTO.ParameterScore;
import com.tulip.host.data.StudentBehaviourReviewDTO.SubjectReview;
import com.tulip.host.data.StudentBehaviourReviewDTO.WeekReview;
import com.tulip.host.domain.AssessmentParameter;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.ClassSubject;
import com.tulip.host.domain.ClassroomAcademicUpload;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.StudentBehaviourScore;
import com.tulip.host.domain.UploadRecord;
import com.tulip.host.enums.AcademicUploadType;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.ClassSubjectRepository;
import com.tulip.host.repository.ClassroomAcademicUploadRepository;
import com.tulip.host.repository.StudentBehaviourScoreRepository;
import com.tulip.host.repository.UploadRecordRepository;
import com.tulip.host.utils.ExcelTemplateStyle;
import com.tulip.host.utils.ExcelUploadValidator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BehaviourService {

    private final ClassDetailRepository classDetailRepository;
    private final ClassSubjectRepository classSubjectRepository;
    private final ClassroomAcademicUploadRepository academicUploadRepository;
    private final StudentBehaviourScoreRepository behaviourScoreRepository;
    private final UploadService uploadService;
    private final UploadRecordRepository uploadRecordRepository;

    // ─────────────────────────────────────────────────────────────
    // CLASSROOM INSIGHTS
    // ─────────────────────────────────────────────────────────────

    private static final double LOW_BEHAVIOUR_THRESHOLD = 4.0; // out of 10

    @Transactional(readOnly = true)
    public List<BehaviourSubjectInsightDTO> getClassroomBehaviourInsights(Long classroomId) {
        List<StudentBehaviourScore> allScores = behaviourScoreRepository.findAllByClassroomId(classroomId);
        if (allScores.isEmpty()) return List.of();

        // Group by subject
        Map<String, List<StudentBehaviourScore>> bySubject = allScores
            .stream()
            .collect(Collectors.groupingBy(s -> s.getAcademicUpload().getSubjectKey()));

        return bySubject
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                String subjectKey = entry.getKey();
                List<StudentBehaviourScore> subjectScores = entry.getValue();

                // Count distinct weeks for this subject
                long weekCount = subjectScores.stream().map(s -> s.getAcademicUpload().getWeekStartDate()).distinct().count();

                // Per-student avg score
                Map<Long, List<StudentBehaviourScore>> byStudent = subjectScores
                    .stream()
                    .collect(Collectors.groupingBy(s -> s.getStudent().getId()));

                double subjectAvg = subjectScores.stream().mapToInt(s -> s.getScore()).average().orElse(0);

                List<BehaviourStudentInsightDTO> lowStudents = byStudent
                    .entrySet()
                    .stream()
                    .map(se -> {
                        double avg = se.getValue().stream().mapToInt(s -> s.getScore()).average().orElse(0);
                        String name = se.getValue().get(0).getStudent().getName();
                        return BehaviourStudentInsightDTO.builder()
                            .studentId(se.getKey())
                            .studentName(name)
                            .avgScore(Math.round(avg * 10.0) / 10.0)
                            .build();
                    })
                    .filter(s -> s.getAvgScore() < LOW_BEHAVIOUR_THRESHOLD)
                    .sorted(Comparator.comparingDouble(BehaviourStudentInsightDTO::getAvgScore))
                    .collect(Collectors.toList());

                return BehaviourSubjectInsightDTO.builder()
                    .subjectKey(subjectKey)
                    .weekCount((int) weekCount)
                    .avgScore(Math.round(subjectAvg * 10.0) / 10.0)
                    .lowScoreStudents(lowStudents)
                    .build();
            })
            .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // TEMPLATE GENERATION
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] generateTemplate(Long classroomId, String subjectKey, LocalDate weekStart) throws IOException {
        ClassSubject subject = classSubjectRepository
            .findByClassDetailIdAndSubjectKey(classroomId, subjectKey)
            .orElseThrow(() -> new IllegalArgumentException("Subject not configured for classroom: " + subjectKey));

        List<AssessmentParameter> params = subject
            .getAssessmentParams()
            .stream()
            .sorted(Comparator.comparing(AssessmentParameter::getName))
            .collect(Collectors.toList());

        ClassDetail classDetail = classDetailRepository.findByClass(classroomId);
        if (classDetail == null) {
            throw new IllegalArgumentException("Classroom not found: " + classroomId);
        }

        List<Student> students = classDetail
            .getStudents()
            .stream()
            .filter(s -> Boolean.TRUE.equals(s.getActive()))
            .sorted(Comparator.comparing(s -> s.getName().toUpperCase()))
            .collect(Collectors.toList());

        return buildTemplate(students, params, subjectKey, weekStart, classroomId, classDetail.getStd());
    }

    private byte[] buildTemplate(
        List<Student> students,
        List<AssessmentParameter> params,
        String subjectKey,
        LocalDate weekStart,
        Long classroomId,
        String std
    ) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Behaviour - " + subjectKey);

            // ── Styles via shared factory ─────────────────────────────
            CellStyle infoLabel = ExcelTemplateStyle.infoLabel(workbook);
            CellStyle infoValue = ExcelTemplateStyle.infoValue(workbook);
            CellStyle headerStyle = ExcelTemplateStyle.columnHeader(workbook);
            CellStyle dataStyle = ExcelTemplateStyle.dataCell(workbook);

            // ── Row 0: info banner ────────────────────────────────────
            ExcelTemplateStyle.writeInfoBanner(
                sheet.createRow(0),
                subjectKey,
                weekStart.format(DateTimeFormatter.ISO_LOCAL_DATE),
                classroomId,
                std,
                infoLabel,
                infoValue
            );

            // ── Row 1: column headers ─────────────────────────────────
            Row header = sheet.createRow(1);
            header.setHeightInPoints(20);
            ExcelTemplateStyle.styledCell(header, 0, "Student ID", headerStyle);
            ExcelTemplateStyle.styledCell(header, 1, "Student Name", headerStyle);
            for (int i = 0; i < params.size(); i++) {
                ExcelTemplateStyle.styledCell(header, i + 2, params.get(i).getName(), headerStyle);
            }

            // ── Row 2+: one row per student ───────────────────────────
            int rowIdx = 2;
            for (Student student : students) {
                Row row = sheet.createRow(rowIdx++);
                ExcelTemplateStyle.numericCell(row, 0, student.getId(), dataStyle);
                ExcelTemplateStyle.styledCell(row, 1, student.getName(), dataStyle);
            }

            sheet.createFreezePane(0, 2);
            for (int i = 0; i < Math.max(4, params.size() + 2); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UPLOAD & PARSE
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public AcademicUploadDTO processUpload(Long classroomId, MultipartFile file) throws Exception {
        ExcelUploadValidator.validateFile(file);
        String[] meta = readMetadata(file);
        String subjectKey = meta[0];
        LocalDate weekStart = LocalDate.parse(meta[1], DateTimeFormatter.ISO_LOCAL_DATE);
        String fileClassroomId = meta[2];
        if (!fileClassroomId.isEmpty() && !fileClassroomId.equals(String.valueOf(classroomId))) {
            throw new IllegalArgumentException(
                "This file was generated for classroom ID " +
                fileClassroomId +
                " but you are uploading to classroom ID " +
                classroomId +
                ". Please download the correct template for this class."
            );
        }

        if (
            academicUploadRepository
                .findByClassDetailIdAndSubjectKeyAndWeekStartDateAndUploadType(
                    classroomId,
                    subjectKey,
                    weekStart,
                    AcademicUploadType.BEHAVIOUR
                )
                .isPresent()
        ) {
            throw new IllegalStateException("Behaviour scores already uploaded for this classroom, subject and week.");
        }

        ClassSubject subject = classSubjectRepository
            .findByClassDetailIdAndSubjectKey(classroomId, subjectKey)
            .orElseThrow(() -> new IllegalArgumentException("Subject not configured: " + subjectKey));

        List<AssessmentParameter> params = subject
            .getAssessmentParams()
            .stream()
            .sorted(Comparator.comparing(AssessmentParameter::getName))
            .collect(Collectors.toList());

        ClassDetail classDetail = classDetailRepository
            .findById(classroomId)
            .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomId));

        Map<Long, Student> studentMap = classDetail
            .getStudents()
            .stream()
            .filter(s -> Boolean.TRUE.equals(s.getActive()))
            .collect(Collectors.toMap(Student::getId, s -> s));

        String fileUid = uploadService.save(file, "academic/behaviour");
        UploadRecord uploadRecord = uploadRecordRepository.save(
            UploadRecord.builder()
                .uid(fileUid)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size((int) file.getSize())
                .status("done")
                .uploadType("behaviour")
                .build()
        );

        ClassroomAcademicUpload upload = ClassroomAcademicUpload.builder()
            .classDetail(classDetail)
            .subjectKey(subjectKey)
            .weekStartDate(weekStart)
            .uploadType(AcademicUploadType.BEHAVIOUR)
            .s3Upload(uploadRecord)
            .build();
        upload = academicUploadRepository.save(upload);

        List<StudentBehaviourScore> scores = parseScores(file, params, studentMap, upload);
        behaviourScoreRepository.saveAll(scores);

        return toDTO(upload);
    }

    private List<StudentBehaviourScore> parseScores(
        MultipartFile file,
        List<AssessmentParameter> params,
        Map<Long, Student> studentMap,
        ClassroomAcademicUpload upload
    ) throws IOException {
        List<StudentBehaviourScore> result = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()))) {
            Sheet sheet = workbook.getSheetAt(0);
            ExcelUploadValidator.requireMinDataRows(sheet, 2);
            for (int rowIdx = 2; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;
                Cell idCell = row.getCell(0);
                if (idCell == null) continue;
                long studentId = (long) idCell.getNumericCellValue();
                Student student = studentMap.get(studentId);
                if (student == null) {
                    log.warn("Student ID {} from template not found in classroom", studentId);
                    continue;
                }
                for (int paramIdx = 0; paramIdx < params.size(); paramIdx++) {
                    Cell cell = row.getCell(paramIdx + 2);
                    String paramName = params.get(paramIdx).getName();
                    if (cell == null || cell.getCellType() == CellType.BLANK) {
                        errors.add(student.getName() + " → " + paramName + ": score is required");
                        continue;
                    }
                    byte score = readScore(cell);
                    if (score < 1 || score > 10) {
                        errors.add(student.getName() + " → " + paramName + ": score " + score + " is out of range (must be 1–10)");
                    } else {
                        result.add(
                            StudentBehaviourScore.builder()
                                .academicUpload(upload)
                                .student(student)
                                .assessmentParameter(params.get(paramIdx))
                                .score(score)
                                .build()
                        );
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("File validation failed — fix the following and re-upload:\n" + String.join("\n", errors));
        }
        return result;
    }

    private String[] readMetadata(MultipartFile file) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row meta = sheet.getRow(0);
            if (meta == null) throw new IllegalArgumentException("Template metadata row is missing.");
            String subjectKey = meta.getCell(1).getStringCellValue().trim();
            Cell weekCell = meta.getCell(3);
            String weekStart;
            if (weekCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(weekCell)) {
                weekStart = weekCell.getLocalDateTimeCellValue().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            } else {
                weekStart = weekCell.getStringCellValue().trim();
            }
            if (subjectKey.isEmpty() || weekStart.isEmpty()) {
                throw new IllegalArgumentException("Subject or Week is missing in template metadata row.");
            }
            // Cell 5: ClassroomID (present in templates downloaded after classroom-guard was added)
            String classroomId = "";
            Cell classroomIdCell = meta.getCell(5);
            if (classroomIdCell != null) {
                if (classroomIdCell.getCellType() == CellType.NUMERIC) {
                    classroomId = String.valueOf((long) classroomIdCell.getNumericCellValue());
                } else if (classroomIdCell.getCellType() == CellType.STRING) {
                    classroomId = classroomIdCell.getStringCellValue().trim();
                }
            }
            return new String[] { subjectKey, weekStart, classroomId };
        }
    }

    private void styledCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private byte readScore(Cell cell) {
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (byte) cell.getNumericCellValue();
            }
            if (cell.getCellType() == CellType.STRING) {
                String val = cell.getStringCellValue().trim();
                return val.isEmpty() ? 0 : Byte.parseByte(val);
            }
        } catch (Exception e) {
            // ignore malformed cells
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────────
    // HISTORY
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AcademicUploadDTO> getHistory(Long classroomId) {
        return academicUploadRepository
            .findByClassDetailIdAndUploadTypeOrderByWeekStartDateDesc(classroomId, AcademicUploadType.BEHAVIOUR)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    private AcademicUploadDTO toDTO(ClassroomAcademicUpload entity) {
        return AcademicUploadDTO.builder()
            .id(entity.getId())
            .classroomId(entity.getClassDetail().getId())
            .subjectKey(entity.getSubjectKey())
            .weekStartDate(entity.getWeekStartDate())
            .uploadType(entity.getUploadType().name())
            .fileUid(entity.getS3Upload() != null ? entity.getS3Upload().getUid() : null)
            .uploadedBy(entity.getCreatedBy())
            .createdDate(entity.getCreatedDate() != null ? entity.getCreatedDate().toString() : null)
            .build();
    }

    // ─────────────────────────────────────────────────────────────
    // STUDENT BEHAVIOUR REVIEW
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public StudentBehaviourReviewDTO getStudentReview(Long studentId) {
        List<StudentBehaviourScore> scores = behaviourScoreRepository.findByStudentIdWithDetails(studentId);

        // Group by subjectKey → weekStartDate → list of scores
        Map<String, Map<LocalDate, List<StudentBehaviourScore>>> bySubjectByWeek = scores
            .stream()
            .collect(
                Collectors.groupingBy(
                    s -> s.getAcademicUpload().getSubjectKey(),
                    LinkedHashMap::new,
                    Collectors.groupingBy(s -> s.getAcademicUpload().getWeekStartDate(), LinkedHashMap::new, Collectors.toList())
                )
            );

        List<SubjectReview> subjects = bySubjectByWeek
            .entrySet()
            .stream()
            .map(subjectEntry -> {
                String subjectKey = subjectEntry.getKey();

                List<WeekReview> weeks = subjectEntry
                    .getValue()
                    .entrySet()
                    .stream()
                    .map(weekEntry -> {
                        LocalDate weekStart = weekEntry.getKey();
                        List<StudentBehaviourScore> weekScores = weekEntry.getValue();
                        String uploadedBy = weekScores.get(0).getAcademicUpload().getCreatedBy();

                        List<ParameterScore> paramScores = weekScores
                            .stream()
                            .sorted(Comparator.comparing(s -> s.getAssessmentParameter().getName()))
                            .map(s ->
                                ParameterScore.builder().parameterName(s.getAssessmentParameter().getName()).score(s.getScore()).build()
                            )
                            .collect(Collectors.toList());

                        double weekAvg = weekScores.stream().mapToInt(s -> s.getScore()).average().orElse(0.0);

                        return WeekReview.builder()
                            .weekStartDate(weekStart)
                            .uploadedBy(uploadedBy)
                            .avgScore(Math.round(weekAvg * 10.0) / 10.0)
                            .scores(paramScores)
                            .build();
                    })
                    .collect(Collectors.toList());

                double subjectAvg = weeks.stream().mapToDouble(WeekReview::getAvgScore).average().orElse(0.0);

                return SubjectReview.builder().subjectKey(subjectKey).avgScore(Math.round(subjectAvg * 10.0) / 10.0).weeks(weeks).build();
            })
            .collect(Collectors.toList());

        return StudentBehaviourReviewDTO.builder().subjects(subjects).build();
    }
}
