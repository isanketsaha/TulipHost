package com.tulip.host.web.rest;

import com.tulip.host.config.Constants;
import com.tulip.host.data.TransactionReportDTO;
import com.tulip.host.data.TransactionSummary;
import com.tulip.host.service.MonitorService;
import com.tulip.host.service.ProductService;
import com.tulip.host.service.ReportService;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final ReportService dashboardService;
    private final MonitorService monitorService;

    @GetMapping("/finance")
    public TransactionSummary transactionReport(
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") Date from,
        @DateTimeFormat(pattern = "dd-MM-yyyy") @RequestParam Date to,
        @RequestParam(defaultValue = "MONTHLY") String groupByType
    ) {
        return monitorService.transactionReport(
            from,
            to,
            groupByType.equalsIgnoreCase("MONTHLY") ? Constants.GROUP_BY_MONTH_FORMAT : Constants.GROUP_BY_DATE_FORMAT
        );
    }
}
