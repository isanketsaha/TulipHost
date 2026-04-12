package com.tulip.host.service;

import com.tulip.host.data.ClassroomInsightsDTO;
import com.tulip.host.data.ClassroomInsightsDTO.ExamInsightDTO;
import com.tulip.host.data.ClassroomInsightsDTO.StudentInsightDTO;
import com.tulip.host.data.ClassroomInsightsDTO.SubjectInsightDTO;
import com.tulip.host.data.ExamMarksUploadDTO;
import com.tulip.host.data.StudentExamResultDTO;
import com.tulip.host.data.StudentMarkEntryDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.ClassSubject;
import com.tulip.host.domain.ExamMarksUpload;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.StudentExamScore;
import com.tulip.host.domain.UploadRecord;
import com.tulip.host.enums.ExamType;
import com.tulip.host.enums.ScoreType;
import com.tulip.host.enums.TermMarkType;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.ClassSubjectRepository;
import com.tulip.host.repository.ExamMarksUploadRepository;
import com.tulip.host.repository.StudentExamScoreRepository;
import com.tulip.host.repository.UploadRecordRepository;
import com.tulip.host.utils.ExcelTemplateStyle;
import com.tulip.host.utils.ExcelUploadValidator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarksService {

    // Fixed max marks — no DB config needed
    private static final int CT_MAX = 10;
    private static final int TERM_INT_MAX = 10;
    private static final int TERM_EXT_FULL_MAX = 80;
    private static final int TERM_EXT_HALF_MAX = 50;

    private boolean isHalf(ClassSubject s) {
        return s.getTermMarkType() == TermMarkType.HALF;
    }

    private int extMax(ClassSubject s) {
        return isHalf(s) ? TERM_EXT_HALF_MAX : TERM_EXT_FULL_MAX;
    }

    private final ClassDetailRepository classDetailRepository;
    private final ClassSubjectRepository classSubjectRepository;
    private final ExamMarksUploadRepository examUploadRepository;
    private final StudentExamScoreRepository examScoreRepository;
    private final UploadRecordRepository uploadRecordRepository;
    private final UploadService uploadService;

    // ─────────────────────────────────────────────────────────────
    // HISTORY & STUDENT MARKS
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ExamMarksUploadDTO> getExamHistory(Long classroomId) {
        return examUploadRepository
            .findByClassDetailId(classroomId)
            .stream()
            .sorted(Comparator.comparing(ExamMarksUpload::getCreatedDate).reversed())
            .map(u ->
                new ExamMarksUploadDTO(
                    u.getId(),
                    u.getExamType().name(),
                    u.getExamName(),
                    u.getCreatedBy(),
                    u.getCreatedDate() != null ? u.getCreatedDate().toString() : null,
                    u.getS3Upload() != null ? u.getS3Upload().getUid() : null
                )
            )
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentExamResultDTO> getStudentMarks(Long studentId) {
        List<StudentExamScore> scores = examScoreRepository.findByStudentId(studentId);
        Map<ExamMarksUpload, List<StudentExamScore>> byUpload = scores
            .stream()
            .collect(Collectors.groupingBy(StudentExamScore::getExamUpload));

        return byUpload
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(e -> e.getKey().getCreatedDate()))
            .map(e -> {
                ExamMarksUpload upload = e.getKey();
                Long classroomId = upload.getClassDetail().getId();
                List<StudentMarkEntryDTO> entries = e
                    .getValue()
                    .stream()
                    .map(score ->
                        new StudentMarkEntryDTO(
                            score.getSubjectKey(),
                            score.getScoreType().name(),
                            score.getMarks(),
                            computeMaxMarks(classroomId, score.getSubjectKey(), upload.getExamType(), score.getScoreType())
                        )
                    )
                    .sorted(Comparator.comparing(StudentMarkEntryDTO::getSubjectKey))
                    .collect(Collectors.toList());
                return new StudentExamResultDTO(upload.getExamType().name(), upload.getExamName(), entries);
            })
            .collect(Collectors.toList());
    }

    private int computeMaxMarks(Long classroomId, String subjectKey, ExamType examType, ScoreType scoreType) {
        if (examType == ExamType.CT) return CT_MAX;
        if (scoreType == ScoreType.INTERNAL) return TERM_INT_MAX;
        return classSubjectRepository
            .findByClassDetailIdAndSubjectKey(classroomId, subjectKey)
            .map(s -> s.getTermMarkType() == TermMarkType.HALF ? TERM_EXT_HALF_MAX : TERM_EXT_FULL_MAX)
            .orElse(TERM_EXT_FULL_MAX);
    }

    // ─────────────────────────────────────────────────────────────
    // CLASSROOM INSIGHTS
    // ─────────────────────────────────────────────────────────────

    private static final double PASS_THRESHOLD = 33.0;

    @Transactional(readOnly = true)
    public List<ExamInsightDTO> getClassroomExamInsights(Long classroomId) {
        List<StudentExamScore> allScores = examScoreRepository.findAllByClassroomId(classroomId);
        if (allScores.isEmpty()) return List.of();

        // Preload subject configs for max-marks computation
        Map<String, ClassSubject> subjectMap = classSubjectRepository
            .findAllByClassDetailId(classroomId)
            .stream()
            .collect(Collectors.toMap(ClassSubject::getSubjectKey, s -> s));

        // Group by exam upload
        Map<ExamMarksUpload, List<StudentExamScore>> byUpload = allScores
            .stream()
            .collect(Collectors.groupingBy(StudentExamScore::getExamUpload));

        return byUpload
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(e -> e.getKey().getCreatedDate()))
            .map(e -> buildExamInsight(e.getKey(), e.getValue(), subjectMap))
            .collect(Collectors.toList());
    }

    private ExamInsightDTO buildExamInsight(ExamMarksUpload upload, List<StudentExamScore> scores, Map<String, ClassSubject> subjectMap) {
        Long classroomId = upload.getClassDetail().getId();
        ExamType examType = upload.getExamType();

        // Group scores by student
        Map<Long, List<StudentExamScore>> byStudent = scores.stream().collect(Collectors.groupingBy(s -> s.getStudent().getId()));

        // Compute per-student overall percent and weak subjects
        List<StudentInsightDTO> allStudents = byStudent
            .entrySet()
            .stream()
            .map(entry -> {
                List<StudentExamScore> studentScores = entry.getValue();
                String name = studentScores.get(0).getStudent().getName();

                // Group by subject, sum marks and maxMarks
                Map<String, int[]> subjectTotals = new java.util.HashMap<>();
                for (StudentExamScore s : studentScores) {
                    int max = resolveMaxMarks(s.getSubjectKey(), examType, s.getScoreType(), subjectMap);
                    subjectTotals.computeIfAbsent(s.getSubjectKey(), k -> new int[] { 0, 0 });
                    subjectTotals.get(s.getSubjectKey())[0] += s.getMarks();
                    subjectTotals.get(s.getSubjectKey())[1] += max;
                }

                int totalMarks = subjectTotals.values().stream().mapToInt(v -> v[0]).sum();
                int totalMax = subjectTotals.values().stream().mapToInt(v -> v[1]).sum();
                double overallPct = totalMax > 0 ? ((totalMarks * 100.0) / totalMax) : 0;

                List<String> weakSubjects = subjectTotals
                    .entrySet()
                    .stream()
                    .filter(s -> s.getValue()[1] > 0 && (s.getValue()[0] * 100.0) / s.getValue()[1] < PASS_THRESHOLD)
                    .map(Map.Entry::getKey)
                    .sorted()
                    .collect(Collectors.toList());

                return StudentInsightDTO.builder()
                    .studentId(entry.getKey())
                    .studentName(name)
                    .scorePercent(Math.round(overallPct * 10.0) / 10.0)
                    .weakSubjects(weakSubjects)
                    .build();
            })
            .collect(Collectors.toList());

        int passCount = (int) allStudents.stream().filter(s -> s.getScorePercent() >= PASS_THRESHOLD).count();
        double passPercent = allStudents.isEmpty() ? 0 : Math.round((passCount * 1000.0) / allStudents.size()) / 10.0;

        List<StudentInsightDTO> lowPerformers = allStudents
            .stream()
            .filter(s -> s.getScorePercent() < PASS_THRESHOLD)
            .sorted(Comparator.comparingDouble(StudentInsightDTO::getScorePercent))
            .collect(Collectors.toList());

        // Per-subject aggregation
        Map<String, List<StudentExamScore>> bySubject = scores.stream().collect(Collectors.groupingBy(StudentExamScore::getSubjectKey));

        List<SubjectInsightDTO> subjects = bySubject
            .entrySet()
            .stream()
            .map(entry -> {
                String subjectKey = entry.getKey();
                List<StudentExamScore> subjectScores = entry.getValue();
                int max = resolveMaxMarks(subjectKey, examType, subjectScores.get(0).getScoreType(), subjectMap);

                // Group by student to get per-student subject total
                Map<Long, Integer> studentSubjectMarks = subjectScores
                    .stream()
                    .collect(Collectors.groupingBy(s -> s.getStudent().getId(), Collectors.summingInt(s -> (int) s.getMarks())));
                // Max per student = sum of max for all scoreTypes of this subject
                Map<Long, Integer> studentSubjectMax = new java.util.HashMap<>();
                for (StudentExamScore s : subjectScores) {
                    int m = resolveMaxMarks(subjectKey, examType, s.getScoreType(), subjectMap);
                    studentSubjectMax.merge(s.getStudent().getId(), m, Integer::sum);
                }

                int subjectPassCount = 0;
                double totalPct = 0;
                for (Long sid : studentSubjectMarks.keySet()) {
                    int sMax = studentSubjectMax.getOrDefault(sid, max);
                    double pct = sMax > 0 ? (studentSubjectMarks.get(sid) * 100.0) / sMax : 0;
                    totalPct += pct;
                    if (pct >= PASS_THRESHOLD) subjectPassCount++;
                }
                double avgPct = studentSubjectMarks.isEmpty() ? 0 : Math.round((totalPct / studentSubjectMarks.size()) * 10.0) / 10.0;

                return SubjectInsightDTO.builder()
                    .subjectKey(subjectKey)
                    .avgPercent(avgPct)
                    .passCount(subjectPassCount)
                    .totalCount(studentSubjectMarks.size())
                    .build();
            })
            .sorted(Comparator.comparing(SubjectInsightDTO::getSubjectKey))
            .collect(Collectors.toList());

        return ExamInsightDTO.builder()
            .examName(upload.getExamName())
            .examType(upload.getExamType().name())
            .totalStudents(allStudents.size())
            .passCount(passCount)
            .passPercent(passPercent)
            .subjects(subjects)
            .lowPerformers(lowPerformers)
            .build();
    }

    private int resolveMaxMarks(String subjectKey, ExamType examType, ScoreType scoreType, Map<String, ClassSubject> subjectMap) {
        if (examType == ExamType.CT) return CT_MAX;
        if (scoreType == ScoreType.INTERNAL) return TERM_INT_MAX;
        ClassSubject cs = subjectMap.get(subjectKey);
        if (cs == null) return TERM_EXT_FULL_MAX;
        return cs.getTermMarkType() == TermMarkType.HALF ? TERM_EXT_HALF_MAX : TERM_EXT_FULL_MAX;
    }

    // ─────────────────────────────────────────────────────────────
    // TEMPLATE GENERATION
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] generateTemplate(Long classroomId, ExamType examType, String examName) throws IOException {
        ClassDetail classDetail = classDetailRepository.findByClass(classroomId);
        if (classDetail == null) {
            throw new IllegalArgumentException("Classroom not found: " + classroomId);
        }

        List<ClassSubject> subjects = classSubjectRepository
            .findByClassDetailIdAndExamType(classroomId, examType)
            .stream()
            .sorted(Comparator.comparing(ClassSubject::getSubjectKey))
            .collect(Collectors.toList());

        List<Student> students = classDetail
            .getStudents()
            .stream()
            .filter(s -> Boolean.TRUE.equals(s.getActive()))
            .sorted(Comparator.comparing(s -> s.getName().toUpperCase()))
            .collect(Collectors.toList());

        return examType == ExamType.CT
            ? buildCtTemplate(classroomId, classDetail.getStd(), examType, examName, subjects, students)
            : buildTermTemplate(classroomId, classDetail.getStd(), examType, examName, subjects, students);
    }

    private byte[] buildCtTemplate(
        Long classroomId,
        String std,
        ExamType examType,
        String examName,
        List<ClassSubject> subjects,
        List<Student> students
    ) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Marks - " + examName);

            CellStyle infoLabel = ExcelTemplateStyle.infoLabel(wb);
            CellStyle infoValue = ExcelTemplateStyle.infoValue(wb);
            CellStyle headerStyle = ExcelTemplateStyle.columnHeader(wb);
            CellStyle dataStyle = ExcelTemplateStyle.dataCell(wb);

            // Row 0: info banner (cell positions match readMetadata — do not reorder)
            ExcelTemplateStyle.writeMarksBanner(sheet.createRow(0), classroomId, examType.name(), examName, std, infoLabel, infoValue);

            // Row 1: column headers
            Row header = sheet.createRow(1);
            header.setHeightInPoints(20);
            styledCell(header, 0, "Student ID", headerStyle);
            styledCell(header, 1, "Student Name", headerStyle);
            for (int i = 0; i < subjects.size(); i++) {
                styledCell(header, i + 2, label(subjects.get(i)) + " (/" + CT_MAX + ")", headerStyle);
            }

            // Row 2+: students
            int rowIdx = 2;
            for (Student s : students) {
                Row row = sheet.createRow(rowIdx++);
                ExcelTemplateStyle.numericCell(row, 0, s.getId(), dataStyle);
                ExcelTemplateStyle.styledCell(row, 1, s.getName(), dataStyle);
            }

            sheet.createFreezePane(0, 2);
            autoSize(sheet, subjects.size() + 2);
            wb.write(out);
            return out.toByteArray();
        }
    }

    private byte[] buildTermTemplate(
        Long classroomId,
        String std,
        ExamType examType,
        String examName,
        List<ClassSubject> subjects,
        List<Student> students
    ) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Marks - " + examName);

            CellStyle infoLabel = ExcelTemplateStyle.infoLabel(wb);
            CellStyle infoValue = ExcelTemplateStyle.infoValue(wb);
            CellStyle headerStyle = ExcelTemplateStyle.columnHeader(wb);
            CellStyle subjectHeaderStyle = ExcelTemplateStyle.groupHeader(wb);
            CellStyle dataStyle = ExcelTemplateStyle.dataCell(wb);

            // Row 0: info banner (cell positions match readMetadata — do not reorder)
            ExcelTemplateStyle.writeMarksBanner(sheet.createRow(0), classroomId, examType.name(), examName, std, infoLabel, infoValue);

            // Row 1: subject name headers (FULL spans 2 cols, HALF spans 1 col)
            Row subjectRow = sheet.createRow(1);
            styledCell(subjectRow, 0, "Student ID", headerStyle);
            styledCell(subjectRow, 1, "Student Name", headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));

            int col = 2;
            for (ClassSubject subject : subjects) {
                styledCell(subjectRow, col, label(subject), subjectHeaderStyle);
                if (isHalf(subject)) {
                    // Single col — merge across both header rows
                    sheet.addMergedRegion(new CellRangeAddress(1, 2, col, col));
                    col += 1;
                } else {
                    // Two cols — Internal + External
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, col, col + 1));
                    col += 2;
                }
            }

            // Row 2: Internal/External sub-headers (only for FULL subjects; HALF merged above)
            Row subRow = sheet.createRow(2);
            col = 2;
            for (ClassSubject subject : subjects) {
                if (isHalf(subject)) {
                    styledCell(subRow, col, "External (/" + TERM_EXT_HALF_MAX + ")", headerStyle);
                    col += 1;
                } else {
                    styledCell(subRow, col, "Internal (/" + TERM_INT_MAX + ")", headerStyle);
                    styledCell(subRow, col + 1, "External (/" + TERM_EXT_FULL_MAX + ")", headerStyle);
                    col += 2;
                }
            }

            // Row 3+: students
            int rowIdx = 3;
            for (Student s : students) {
                Row row = sheet.createRow(rowIdx++);
                ExcelTemplateStyle.numericCell(row, 0, s.getId(), dataStyle);
                ExcelTemplateStyle.styledCell(row, 1, s.getName(), dataStyle);
            }

            sheet.createFreezePane(0, 3);
            autoSize(sheet, col);
            wb.write(out);
            return out.toByteArray();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UPLOAD & PARSE
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public void processUpload(Long classroomId, ExamType examType, String examName, MultipartFile file) throws Exception {
        ExcelUploadValidator.validateFile(file);
        // Validate metadata in the file matches the request
        String[] meta = readMetadata(file);
        String fileClassroomId = meta[0];
        String fileExamType = meta[1];
        String fileExamName = meta[2];

        if (!fileClassroomId.isEmpty() && !fileClassroomId.equals(String.valueOf(classroomId))) {
            throw new IllegalArgumentException(
                "File was generated for classroom " +
                fileClassroomId +
                " but uploading to classroom " +
                classroomId +
                ". Download the correct template."
            );
        }
        if (!fileExamType.equalsIgnoreCase(examType.name())) {
            throw new IllegalArgumentException("File exam type '" + fileExamType + "' does not match '" + examType.name() + "'.");
        }
        if (!fileExamName.equalsIgnoreCase(examName)) {
            throw new IllegalArgumentException("File exam name '" + fileExamName + "' does not match '" + examName + "'.");
        }

        // Duplicate check
        if (examUploadRepository.findByClassDetailIdAndExamTypeAndExamName(classroomId, examType, examName).isPresent()) {
            throw new IllegalStateException("Marks already uploaded for " + examName + " in this classroom.");
        }

        // Load classroom and subjects
        ClassDetail classDetail = classDetailRepository
            .findById(classroomId)
            .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomId));

        List<ClassSubject> subjects = classSubjectRepository
            .findByClassDetailIdAndExamType(classroomId, examType)
            .stream()
            .sorted(Comparator.comparing(ClassSubject::getSubjectKey))
            .collect(Collectors.toList());

        Map<Long, Student> studentMap = classDetail
            .getStudents()
            .stream()
            .filter(s -> Boolean.TRUE.equals(s.getActive()))
            .collect(Collectors.toMap(Student::getId, s -> s));

        // Save file to S3
        String fileUid = uploadService.save(file, "academic/marks");
        UploadRecord uploadRecord = uploadRecordRepository.save(
            UploadRecord.builder()
                .uid(fileUid)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size((int) file.getSize())
                .status("done")
                .uploadType("marks")
                .build()
        );

        ExamMarksUpload upload = examUploadRepository.save(
            ExamMarksUpload.builder().classDetail(classDetail).examType(examType).examName(examName).s3Upload(uploadRecord).build()
        );

        List<StudentExamScore> scores = examType == ExamType.CT
            ? parseCtScores(file, subjects, studentMap, upload)
            : parseTermScores(file, subjects, studentMap, upload);

        examScoreRepository.saveAll(scores);
    }

    private List<StudentExamScore> parseCtScores(
        MultipartFile file,
        List<ClassSubject> subjects,
        Map<Long, Student> studentMap,
        ExamMarksUpload upload
    ) throws IOException {
        List<StudentExamScore> result = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        // CT: data starts at row 2 (row 0=meta, row 1=header)
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()))) {
            Sheet sheet = wb.getSheetAt(0);
            ExcelUploadValidator.requireMinDataRows(sheet, 2);
            for (int rowIdx = 2; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;
                Student student = resolveStudent(row, studentMap);
                if (student == null) continue;
                for (int i = 0; i < subjects.size(); i++) {
                    Cell cell = row.getCell(i + 2);
                    String subjectKey = subjects.get(i).getSubjectKey();
                    Short marks = readMarks(cell, student.getName(), subjectKey, "Total", CT_MAX, errors);
                    if (marks != null) {
                        result.add(
                            StudentExamScore.builder()
                                .examUpload(upload)
                                .student(student)
                                .subjectKey(subjectKey)
                                .scoreType(ScoreType.TOTAL)
                                .marks(marks)
                                .build()
                        );
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed:\n" + String.join("\n", errors));
        }
        return result;
    }

    private List<StudentExamScore> parseTermScores(
        MultipartFile file,
        List<ClassSubject> subjects,
        Map<Long, Student> studentMap,
        ExamMarksUpload upload
    ) throws IOException {
        List<StudentExamScore> result = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        // TERM: data starts at row 3 (row 0=meta, row 1=subject headers, row 2=sub-headers)
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()))) {
            Sheet sheet = wb.getSheetAt(0);
            ExcelUploadValidator.requireMinDataRows(sheet, 3);
            for (int rowIdx = 3; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;
                Student student = resolveStudent(row, studentMap);
                if (student == null) continue;

                int col = 2;
                for (ClassSubject subject : subjects) {
                    String subjectKey = subject.getSubjectKey();
                    if (isHalf(subject)) {
                        // HALF: single External column only
                        Short external = readMarks(row.getCell(col), student.getName(), subjectKey, "External", TERM_EXT_HALF_MAX, errors);
                        if (external != null) {
                            result.add(
                                StudentExamScore.builder()
                                    .examUpload(upload)
                                    .student(student)
                                    .subjectKey(subjectKey)
                                    .scoreType(ScoreType.EXTERNAL)
                                    .marks(external)
                                    .build()
                            );
                        }
                        col += 1;
                    } else {
                        // FULL: Internal + External columns
                        Short internal = readMarks(row.getCell(col), student.getName(), subjectKey, "Internal", TERM_INT_MAX, errors);
                        Short external = readMarks(
                            row.getCell(col + 1),
                            student.getName(),
                            subjectKey,
                            "External",
                            TERM_EXT_FULL_MAX,
                            errors
                        );
                        if (internal != null) {
                            result.add(
                                StudentExamScore.builder()
                                    .examUpload(upload)
                                    .student(student)
                                    .subjectKey(subjectKey)
                                    .scoreType(ScoreType.INTERNAL)
                                    .marks(internal)
                                    .build()
                            );
                        }
                        if (external != null) {
                            result.add(
                                StudentExamScore.builder()
                                    .examUpload(upload)
                                    .student(student)
                                    .subjectKey(subjectKey)
                                    .scoreType(ScoreType.EXTERNAL)
                                    .marks(external)
                                    .build()
                            );
                        }
                        col += 2;
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed:\n" + String.join("\n", errors));
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────

    private String[] readMetadata(MultipartFile file) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()))) {
            Sheet sheet = wb.getSheetAt(0);
            Row meta = sheet.getRow(0);
            if (meta == null) throw new IllegalArgumentException("Template metadata row is missing.");
            String classroomId = cellString(meta.getCell(1));
            String examType = cellString(meta.getCell(3));
            String examName = cellString(meta.getCell(5));
            return new String[] { classroomId, examType, examName };
        }
    }

    private String cellString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return cell.getStringCellValue().trim();
    }

    private Student resolveStudent(Row row, Map<Long, Student> studentMap) {
        Cell idCell = row.getCell(0);
        if (idCell == null) return null;
        long studentId = (long) idCell.getNumericCellValue();
        Student student = studentMap.get(studentId);
        if (student == null) log.warn("Student ID {} from template not found in classroom", studentId);
        return student;
    }

    private Short readMarks(Cell cell, String studentName, String subjectKey, String component, int max, List<String> errors) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errors.add(studentName + " → " + subjectKey + " " + component + ": marks are required");
            return null;
        }
        short val;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                val = (short) cell.getNumericCellValue();
            } else {
                val = Short.parseShort(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            errors.add(studentName + " → " + subjectKey + " " + component + ": invalid value");
            return null;
        }
        if (val < 0 || val > max) {
            errors.add(studentName + " → " + subjectKey + " " + component + ": " + val + " out of range (0–" + max + ")");
            return null;
        }
        return val;
    }

    private String label(ClassSubject s) {
        return s.getDisplayName() != null && !s.getDisplayName().isBlank() ? s.getDisplayName() : s.getSubjectKey();
    }

    private void styledCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void autoSize(Sheet sheet, int colCount) {
        for (int i = 0; i < Math.max(4, colCount); i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
