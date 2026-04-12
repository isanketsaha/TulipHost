package com.tulip.host.utils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Shared Excel style factory for all academic upload templates.
 *
 * All templates share the same visual language:
 *   Row 0  — Info banner  (grey, shows Class / Week / Subject / Classroom ID)
 *   Row 1  — Column headers (maroon background, white bold text)
 *   Row 2+ — Data rows
 *
 * Usage: call the static factory methods once per workbook, then reuse the
 * returned CellStyle across all rows — POI has a per-workbook style limit.
 */
public final class ExcelTemplateStyle {

    // TES brand colours
    private static final byte[] MAROON = { (byte) 0xB1, (byte) 0x4B, (byte) 0x34 };
    private static final byte[] GOLD = { (byte) 0xC9, (byte) 0xA8, (byte) 0x4C };
    private static final byte[] LIGHT_GREY_BG = { (byte) 0xF2, (byte) 0xF2, (byte) 0xF2 };

    private ExcelTemplateStyle() {}

    // ── Info banner ───────────────────────────────────────────────
    // Small grey row at the top of every template.
    // Key cells (Subject, Week, etc.) use infoLabel; value cells use infoValue.

    public static CellStyle infoLabel(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 9);
        f.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    public static CellStyle infoValue(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) 9);
        f.setBold(true);
        f.setColor(IndexedColors.GREY_80_PERCENT.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    // ── Column header — maroon ────────────────────────────────────
    // Used for the main data-column header row across all templates.

    public static XSSFCellStyle columnHeader(XSSFWorkbook wb) {
        XSSFCellStyle s = (XSSFCellStyle) wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(MAROON, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBottomBorderColor(IndexedColors.WHITE.getIndex());
        return s;
    }

    // ── Group header — gold ───────────────────────────────────────
    // Used for the subject-name row in Term exam templates (above Internal/External).

    public static XSSFCellStyle groupHeader(XSSFWorkbook wb) {
        XSSFCellStyle s = (XSSFCellStyle) wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(GOLD, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        return s;
    }

    // ── Parameter label — light grey ──────────────────────────────
    // Used for read-only row label column in the planner template.

    public static XSSFCellStyle paramLabel(XSSFWorkbook wb) {
        XSSFCellStyle s = (XSSFCellStyle) wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(LIGHT_GREY_BG, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setWrapText(true);
        s.setBorderRight(BorderStyle.THIN);
        s.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setBorderBottom(BorderStyle.THIN);
        s.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return s;
    }

    // ── Input cell ────────────────────────────────────────────────
    // Used for teacher-filled cells in the planner template.

    public static CellStyle inputCell(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        s.setWrapText(true);
        s.setVerticalAlignment(VerticalAlignment.TOP);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setBorderRight(BorderStyle.THIN);
        s.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return s;
    }

    // ── Data cell ─────────────────────────────────────────────────
    // Used for student attendance cells (blank = teacher fills P/A).

    public static CellStyle dataCell(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setBorderRight(BorderStyle.THIN);
        s.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return s;
    }

    // ── Holiday cell ──────────────────────────────────────────────

    public static CellStyle holidayCell(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setLocked(true);
        return s;
    }

    // ── Leave cell ────────────────────────────────────────────────

    public static CellStyle leaveCell(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setLocked(true);
        return s;
    }

    // ── Info banner writers ───────────────────────────────────────

    /**
     * Writes the standard 8-cell info banner used by Planner and Behaviour templates.
     * Cell positions MUST NOT change — parsing reads subject from [1], week from [3], classroomId from [5].
     */
    public static void writeInfoBanner(
        Row row,
        String subjectKey,
        String week,
        long classroomId,
        String std,
        CellStyle label,
        CellStyle value
    ) {
        row.setHeightInPoints(18);
        styledCell(row, 0, "Subject", label);
        styledCell(row, 1, subjectKey, value);
        styledCell(row, 2, "Week", label);
        styledCell(row, 3, week, value);
        styledCell(row, 4, "Classroom ID", label);
        numericCell(row, 5, classroomId, value);
        styledCell(row, 6, "Class", label);
        styledCell(row, 7, std != null ? std : "", value);
    }

    /**
     * Writes the info banner for the Attendance template.
     * Cell positions MUST NOT change — classroomId is at [5].
     */
    public static void writeAttendanceBanner(Row row, String std, String week, long classroomId, CellStyle label, CellStyle value) {
        row.setHeightInPoints(18);
        styledCell(row, 0, "Class", label);
        styledCell(row, 1, std != null ? std : "", value);
        styledCell(row, 2, "Week", label);
        styledCell(row, 3, week, value);
        styledCell(row, 4, "Classroom ID", label);
        numericCell(row, 5, classroomId, value);
    }

    /**
     * Writes the info banner for Exam Marks templates.
     * Cell positions MUST NOT change — parser reads classroomId from [1], examType from [3], examName from [5].
     */
    public static void writeMarksBanner(
        Row row,
        long classroomId,
        String examType,
        String examName,
        String std,
        CellStyle label,
        CellStyle value
    ) {
        row.setHeightInPoints(18);
        styledCell(row, 0, "Classroom ID", label);
        numericCell(row, 1, classroomId, value);
        styledCell(row, 2, "Exam Type", label);
        styledCell(row, 3, examType, value);
        styledCell(row, 4, "Exam Name", label);
        styledCell(row, 5, examName, value);
        styledCell(row, 6, "Class", label);
        styledCell(row, 7, std != null ? std : "", value);
    }

    // ── Cell helpers ──────────────────────────────────────────────

    public static void styledCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    public static void numericCell(Row row, int col, long value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
