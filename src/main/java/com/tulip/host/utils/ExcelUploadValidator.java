package com.tulip.host.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

/**
 * Ground rules for all academic Excel uploads.
 *
 * Call {@link #validateFile} at the start of every processUpload method before
 * opening the workbook. Add type-specific checks (attendance values, planner
 * required fields) via the dedicated helpers below.
 */
public final class ExcelUploadValidator {

    private static final long MAX_SIZE_BYTES = 2 * 1024 * 1024; // 2 MB

    private static final Set<String> VALID_ATTENDANCE_VALUES = Set.of("", "A", "P", "L", "H");

    private ExcelUploadValidator() {}

    // ─────────────────────────────────────────────────────────────
    // UNIVERSAL — call first in every processUpload
    // ─────────────────────────────────────────────────────────────

    /**
     * Validates the uploaded file before any parsing begins.
     * Throws {@link IllegalArgumentException} with a user-facing message on failure.
     */
    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file received. Please select a file and try again.");
        }

        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        String contentType = file.getContentType() != null ? file.getContentType() : "";

        boolean isXlsx =
            filename.endsWith(".xlsx") || contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        if (!isXlsx) {
            throw new IllegalArgumentException(
                "Invalid file type '" +
                (filename.isEmpty() ? contentType : filename.substring(filename.lastIndexOf('.') + 1).toUpperCase()) +
                "'. Only .xlsx files are accepted."
            );
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("File is too large (" + (file.getSize() / 1024) + " KB). Maximum allowed size is 2 MB.");
        }

        // Verify the file actually parses as a valid XLSX workbook
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()))) {
            if (wb.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException("The uploaded file contains no sheets.");
            }
        } catch (IllegalArgumentException e) {
            throw e; // re-throw our own messages unchanged
        } catch (Exception e) {
            throw new IllegalArgumentException("The file could not be read. It may be corrupted or is not a valid Excel file.");
        }
    }

    /**
     * Ensures there is at least one data row starting at {@code dataStartRow}.
     * Call after opening the sheet, before parsing rows.
     */
    public static void requireMinDataRows(Sheet sheet, int dataStartRow) {
        int lastRow = sheet.getLastRowNum();
        boolean hasData = false;
        for (int i = dataStartRow; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                hasData = true;
                break;
            }
        }
        if (!hasData) {
            throw new IllegalArgumentException("The file has no data rows. Please use the downloaded template and fill in the data.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ATTENDANCE — values must be blank / A / P / L / H only
    // ─────────────────────────────────────────────────────────────

    /**
     * Validates all attendance day-cell values in the sheet.
     * Day columns start at {@code dayColStart} (inclusive).
     * Data rows start at {@code dataStartRow}.
     *
     * Collects all invalid cells before throwing so the teacher gets one
     * consolidated error instead of one-at-a-time failures.
     */
    public static void validateAttendanceValues(Sheet sheet, int dataStartRow, int dayColStart, int dayColEnd) {
        List<String> errors = new ArrayList<>();

        for (int rowIdx = dataStartRow; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row == null) continue;

            // Get student name for readable error messages (col 0)
            String studentName = cellStr(row.getCell(0));
            if (studentName.isEmpty()) continue;

            for (int col = dayColStart; col <= dayColEnd; col++) {
                Cell cell = row.getCell(col);
                String value = cell == null ? "" : cellStr(cell).toUpperCase();
                if (!VALID_ATTENDANCE_VALUES.contains(value)) {
                    // Get header label from row 0 for column reference
                    String colLabel = "Col " + (col + 1);
                    errors.add(studentName + " → " + colLabel + ": '" + value + "' is not valid (use A, P, L, or leave blank)");
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Attendance file has invalid values — fix and re-upload:\n" + String.join("\n", errors));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // PLANNER — Topic / Sub Topic is required
    // ─────────────────────────────────────────────────────────────

    /**
     * Validates that the Topic / Sub Topic field (first data row, col B) is not blank.
     * {@code dataStartRow} is the index of the first field row (row 2 in the planner template).
     */
    public static void validatePlannerRequiredFields(Sheet sheet, int dataStartRow) {
        Row topicRow = sheet.getRow(dataStartRow); // offset 0 → Topic / Sub Topic
        if (topicRow == null || cellStr(topicRow.getCell(1)).isEmpty()) {
            throw new IllegalArgumentException("'Topic / Sub Topic' is required and cannot be blank.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────────────

    private static String cellStr(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue().trim();
                } catch (Exception e) {
                    yield String.valueOf((long) cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }
}
