package com.tulip.host.service;

import com.tulip.host.data.AcademicUploadDTO;
import com.tulip.host.data.PlannerDataDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.ClassroomAcademicUpload;
import com.tulip.host.domain.PlannerEntry;
import com.tulip.host.domain.UploadRecord;
import com.tulip.host.enums.AcademicUploadType;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.ClassroomAcademicUploadRepository;
import com.tulip.host.repository.PlannerEntryRepository;
import com.tulip.host.repository.UploadRecordRepository;
import com.tulip.host.utils.ExcelTemplateStyle;
import com.tulip.host.utils.ExcelUploadValidator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class PlannerService {

    // ── Field keys (stable identifiers stored in DB) ──────────────
    static final String F_TOPIC = "topic_subtopic";
    static final String F_TEACHING_AIDS = "teaching_aids";
    static final String F_ACT_EXPLANATION = "activity_explanation";
    static final String F_ACT_OBJECTIVE = "activity_learning_objective";
    static final String F_LEARNING_OUT = "learning_outcome";
    static final String F_EVALUATION = "evaluation";
    static final String F_HOMEWORK = "homework_worksheet";

    /**
     * Fixed, ordered template row labels. Index maps directly to the data row offset
     * (data starts at row 2 in the Excel sheet). DO NOT reorder — parsing depends on index.
     */
    private static final String[] ROW_LABELS = {
        "Topic / Sub Topic", // offset 0 → F_TOPIC
        "Teaching Aids / Resources", // offset 1 → F_TEACHING_AIDS
        "Activity", // offset 2 → F_ACT_EXPLANATION (col B) + F_ACT_OBJECTIVE (col C)
        "Learning Outcome", // offset 3 → F_LEARNING_OUT
        "Evaluation", // offset 5 → F_EVALUATION
        "Homework / Worksheet", // offset 6 → F_HOMEWORK
    };

    private static final int DATA_START_ROW = 2;
    private static final int ACTIVITY_OFFSET = 2;

    private final ClassDetailRepository classDetailRepository;
    private final ClassroomAcademicUploadRepository academicUploadRepository;
    private final PlannerEntryRepository plannerEntryRepository;
    private final UploadService uploadService;
    private final UploadRecordRepository uploadRecordRepository;

    // ─────────────────────────────────────────────────────────────
    // TEMPLATE GENERATION
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] generateTemplate(Long classroomId, String subjectKey, LocalDate weekStart) throws IOException {
        ClassDetail classDetail = classDetailRepository.findByClass(classroomId);
        if (classDetail == null) throw new IllegalArgumentException("Classroom not found: " + classroomId);
        return buildTemplate(subjectKey, weekStart, classroomId, classDetail.getStd());
    }

    private byte[] buildTemplate(String subjectKey, LocalDate weekStart, Long classroomId, String std) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Planner - " + subjectKey);
            sheet.setDisplayGridlines(true);
            sheet.setPrintGridlines(true);

            // ── Styles via shared factory ─────────────────────────────
            CellStyle infoLabel = ExcelTemplateStyle.infoLabel(workbook);
            CellStyle infoValue = ExcelTemplateStyle.infoValue(workbook);
            CellStyle headerStyle = ExcelTemplateStyle.columnHeader(workbook);
            CellStyle labelStyle = ExcelTemplateStyle.paramLabel(workbook);
            CellStyle inputStyle = ExcelTemplateStyle.inputCell(workbook);

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
            header.setHeightInPoints(24);
            setCellValue(header, 0, "Parameter", headerStyle);
            setCellValue(header, 1, "Details", headerStyle);
            setCellValue(header, 2, "Objective", headerStyle);

            // ── Rows 2+: planner fields ───────────────────────────────
            for (int i = 0; i < ROW_LABELS.length; i++) {
                Row row = sheet.createRow(DATA_START_ROW + i);
                row.setHeightInPoints(60);
                setCellValue(row, 0, ROW_LABELS[i], labelStyle);
                setCellValue(row, 1, "", inputStyle);
                setCellValue(row, 2, "", inputStyle);
            }

            // ── Column widths ─────────────────────────────────────────
            sheet.setColumnWidth(0, 7000); // Parameter — fixed label width
            sheet.setColumnWidth(1, 18000); // Details — wide input area
            sheet.setColumnWidth(2, 14000); // Objective — narrower

            // Freeze info + header rows so both stay visible while scrolling
            sheet.createFreezePane(0, 2);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void setCellValue(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
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
                    AcademicUploadType.PLANNER
                )
                .isPresent()
        ) {
            throw new IllegalStateException("Planner already uploaded for this classroom, subject and week.");
        }

        ClassDetail classDetail = classDetailRepository
            .findById(classroomId)
            .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomId));

        Map<String, String> entries = parseEntries(file);

        String fileUid = uploadService.save(file, "academic/planner");
        UploadRecord uploadRecord = uploadRecordRepository.save(
            UploadRecord.builder()
                .uid(fileUid)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size((int) file.getSize())
                .status("done")
                .uploadType("planner")
                .build()
        );

        ClassroomAcademicUpload upload = academicUploadRepository.save(
            ClassroomAcademicUpload.builder()
                .classDetail(classDetail)
                .subjectKey(subjectKey)
                .weekStartDate(weekStart)
                .uploadType(AcademicUploadType.PLANNER)
                .s3Upload(uploadRecord)
                .build()
        );

        entries.forEach((key, value) ->
            plannerEntryRepository.save(PlannerEntry.builder().academicUpload(upload).fieldKey(key).fieldValue(value).build())
        );

        return toDTO(upload);
    }

    private Map<String, String> parseEntries(MultipartFile file) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()))) {
            Sheet sheet = workbook.getSheetAt(0);
            ExcelUploadValidator.requireMinDataRows(sheet, DATA_START_ROW);
            ExcelUploadValidator.validatePlannerRequiredFields(sheet, DATA_START_ROW);
            Map<String, String> result = new LinkedHashMap<>();

            // Ordered keys matching ROW_LABELS — Activity at offset 2 produces two entries
            String[] keys = { F_TOPIC, F_TEACHING_AIDS, F_ACT_EXPLANATION, F_LEARNING_OUT, F_EVALUATION, F_HOMEWORK };

            for (int i = 0; i < ROW_LABELS.length; i++) {
                Row row = sheet.getRow(DATA_START_ROW + i);
                if (row == null) continue;
                result.put(keys[i], cellStr(row.getCell(1)));
                if (i == ACTIVITY_OFFSET) {
                    result.put(F_ACT_OBJECTIVE, cellStr(row.getCell(2)));
                }
            }
            return result;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // QUERY
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PlannerDataDTO getPlannerData(Long classroomId, String subjectKey, LocalDate weekStart) {
        ClassroomAcademicUpload upload = academicUploadRepository
            .findByClassDetailIdAndSubjectKeyAndWeekStartDateAndUploadType(classroomId, subjectKey, weekStart, AcademicUploadType.PLANNER)
            .orElseThrow(() -> new IllegalArgumentException("No planner found for the given parameters."));

        Map<String, String> entries = plannerEntryRepository
            .findByAcademicUploadId(upload.getId())
            .stream()
            .collect(Collectors.toMap(PlannerEntry::getFieldKey, e -> e.getFieldValue() != null ? e.getFieldValue() : ""));

        return PlannerDataDTO.builder()
            .uploadId(upload.getId())
            .classroomId(classroomId)
            .subjectKey(subjectKey)
            .weekStartDate(weekStart)
            .topicSubTopic(entries.get(F_TOPIC))
            .teachingAids(entries.get(F_TEACHING_AIDS))
            .activityExplanation(entries.get(F_ACT_EXPLANATION))
            .activityLearningObjective(entries.get(F_ACT_OBJECTIVE))
            .learningOutcome(entries.get(F_LEARNING_OUT))
            .evaluation(entries.get(F_EVALUATION))
            .homeworkWorksheet(entries.get(F_HOMEWORK))
            .uploadedBy(upload.getCreatedBy())
            .fileUid(upload.getS3Upload() != null ? upload.getS3Upload().getUid() : null)
            .build();
    }

    @Transactional(readOnly = true)
    public List<AcademicUploadDTO> getHistory(Long classroomId) {
        return academicUploadRepository
            .findByClassDetailIdAndUploadTypeOrderByWeekStartDateDesc(classroomId, AcademicUploadType.PLANNER)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────

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

    private String cellStr(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
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
}
