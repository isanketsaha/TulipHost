package com.tulip.host.web.rest;

import com.tulip.host.service.ExportService;
import com.tulip.host.service.ReportService;
import java.io.IOException;
import java.time.LocalTime;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/stock")
    public void toExcelStock(HttpServletResponse response) throws IOException {
        XSSFWorkbook sheets = exportService.exportStock();
        HttpHeaders header = new HttpHeaders();
        response.setContentType(
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8").toString()
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + "STOCK" + ".xlsx");
        sheets.write(response.getOutputStream());
        sheets.close();
        response.getOutputStream().close();
    }

    @PostMapping("/classStudents")
    public void toExcelStudent(@RequestParam Long classroomId, HttpServletResponse response) throws IOException {
        XSSFWorkbook sheets = exportService.exportClassDetails(classroomId);
        HttpHeaders header = new HttpHeaders();
        response.setContentType(
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8").toString()
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + "STOCK" + ".xlsx");
        sheets.write(response.getOutputStream());
        sheets.close();
        response.getOutputStream().close();
    }
}
