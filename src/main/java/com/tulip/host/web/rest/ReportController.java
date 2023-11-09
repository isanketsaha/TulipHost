package com.tulip.host.web.rest;

import com.tulip.host.data.DashBoardStaffDTO;
import com.tulip.host.data.DashBoardStudentDTO;
import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.service.ProductService;
import com.tulip.host.service.ReportService;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService dashboardService;
    private final ProductService productService;

    @GetMapping("/transaction/{date}")
    public List<PaySummaryDTO> transactionHistory(@Valid @PathVariable Date date) {
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
}
