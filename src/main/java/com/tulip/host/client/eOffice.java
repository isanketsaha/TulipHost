package com.tulip.host.client;

import com.tulip.host.data.AttendanceResponseDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for eOffice service.
 * Requires authentication token configured in application.yml under app.feign.auth.tokens.eoffice
 */
@FeignClient(name = "eOffice", url = "${feign.client.config.eOffice.url}", configuration = FeignClientConfiguration.class)
public interface eOffice {
    /**
     * Download in/out punch data for employee(s) within a date range.
     *
     * @param empcode Employee code (e.g., "0002" or "ALL")
     * @param fromDate Date in dd/MM/yyyy format
     * @param toDate Date in dd/MM/yyyy format
     * @return List of EofficeTime records
     */
    @GetMapping("/DownloadInOutPunchData")
    AttendanceResponseDTO downloadInOutPunchData(
        @RequestParam("Empcode") String empcode,
        @RequestParam("FromDate") String fromDate,
        @RequestParam("ToDate") String toDate
    );
}
