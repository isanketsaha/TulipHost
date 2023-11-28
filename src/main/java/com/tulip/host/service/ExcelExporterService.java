package com.tulip.host.service;

import com.tulip.host.config.Constants;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.utils.CommonUtils;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelExporterService {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private void writeHeaderLine(List<Object> objectList, String sheetName) {
        Object o = objectList.get(0);
        Class<? extends Object> c = o.getClass();
        Field[] declaredFields = c.getDeclaredFields();
        sheet = workbook.createSheet(StringUtils.isNotEmpty(sheetName) ? sheetName : c.getSimpleName().replace("DTO", StringUtils.EMPTY));

        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THICK);
        style.setFont(font);
        style.setLocked(true);
        style.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
        int index = 0;
        createCell(row, index++, "SL", style);
        for (Field field : declaredFields) {
            if (!List.class.isAssignableFrom(field.getType())) {
                String normalString = WordUtils.capitalizeFully(
                    StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(field.getName()), StringUtils.SPACE)
                );
                createCell(row, index++, normalString, style);
            }
        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Date) {
            cell.setCellValue(CommonUtils.formatFromDate((Date) value, "dd-MM-yyyy"));
        } else if (value instanceof PayTypeEnum) {
            cell.setCellValue(((PayTypeEnum) value).toString());
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        }
    }

    private void writeDataLines(List<Object> objectList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        style.setWrapText(true);

        for (Object o : objectList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, rowCount - 1, style);
            Class<? extends Object> c = o.getClass();
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (!List.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        createCell(row, columnCount++, field.get(o), style);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public XSSFWorkbook export(List<Object> objectList, String sheetName) {
        this.workbook = new XSSFWorkbook();
        writeHeaderLine(objectList, sheetName);
        writeDataLines(objectList);
        return workbook;
    }

    public XSSFWorkbook export(XSSFWorkbook workbook, String sheetName, List<Object> objectList) {
        this.workbook = workbook;
        writeHeaderLine(objectList, sheetName);
        writeDataLines(objectList);
        return workbook;
    }
}
