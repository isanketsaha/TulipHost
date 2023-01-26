package com.tulip.host.web.rest;

import com.tulip.host.data.DashBoardStaffDTO;
import com.tulip.host.data.DashBoardStudentDTO;
import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService dashboardService;

    @GetMapping("/transaction")
    public Page<PaySummaryDTO> transactionHistory(
        @RequestParam(value = "page", defaultValue = "0") int pageNo,
        @RequestParam(name = "size", defaultValue = "5") int pageSize
    ) {
        return dashboardService.fetchTransactionHistory(pageNo, pageSize);
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
