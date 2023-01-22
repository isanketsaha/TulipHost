package com.tulip.host.web.rest;

import com.tulip.host.data.DashBoardStaffDTO;
import com.tulip.host.data.DashBoardStudentDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.service.DashboardService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @RequestMapping("/transaction")
    public Page<PaySummaryDTO> transactionHistory(
        @RequestParam(value = "page", defaultValue = "0") int pageNo,
        @RequestParam(name = "size", defaultValue = "5") int pageSize
    ) {
        return dashboardService.fetchTransactionHistory(pageNo, pageSize);
    }

    @RequestMapping("/admission")
    public DashBoardStudentDTO studentReport() {
        return dashboardService.studentReport();
    }

    @RequestMapping("/staff")
    public DashBoardStaffDTO staffReport() {
        return dashboardService.staffReport();
    }
}
