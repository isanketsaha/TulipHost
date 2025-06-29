package com.tulip.host.web.rest;

import static com.tulip.host.config.Constants.DATE_PATTERN;

import com.tulip.host.data.DashBoardStaffDTO;
import com.tulip.host.data.DashBoardStudentDTO;
import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.service.ProductService;
import com.tulip.host.service.ReportService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('UG_ADMIN') or hasAuthority('UG_PRINCIPAL')")
public class ReportController {

    private final ReportService dashboardService;
    private final ProductService productService;

    @GetMapping("/transaction/{date}")
    public List<PaySummaryDTO> transactionHistory(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dashboardService.fetchTransactionHistory(date);
    }

    @GetMapping("/admission")
    public DashBoardStudentDTO studentReport() {
        return dashboardService.studentReport();
    }

    @GetMapping("/staff")
    public DashBoardStaffDTO staffReport() {
        return dashboardService.staffReport();
    }

    @GetMapping("/inventory")
    public List<InventoryItemDTO> inventoryReport() {
        return dashboardService.inventoryReport();
    }

    @GetMapping("/transport")
    public Map<String, Integer> transportReport() {
        return dashboardService.transportReport();
    }

    @GetMapping("/sales")
    public Map<String, Map<String, Double>> salesReport(@RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDate date) {
        return dashboardService.salesReport(date);
    }
}
