package com.tulip.host.web.rest;

import com.tulip.host.service.ExportService;
import com.tulip.host.service.ReportService;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/receipt")
    public void paymentReceipt(@RequestParam Long paymentId, HttpServletResponse response) throws IOException {
        byte[] bytes = exportService.paymentReceipt(paymentId);
        HttpHeaders header = new HttpHeaders();
        response.setContentType(MediaType.APPLICATION_PDF.toString());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=citiesreport.pdf");
        response.getOutputStream().write(bytes);
        response.getOutputStream().close();
    }

    @PostMapping("/transactionHistory")
    public void transactionHistory(
        @RequestBody @DateTimeFormat(pattern = "MMM/YYYY") Map<String, List<Date>> transactionMonths,
        HttpServletResponse response
    ) throws IOException {
        XSSFWorkbook sheets = exportService.transactionHistory(transactionMonths.get("transactionMonths"));
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
