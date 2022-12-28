package com.tulip.host.web.rest;

import com.tulip.host.data.SessionDTO;
import com.tulip.host.service.FinancialYearService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/financialYear")
@RequiredArgsConstructor
public class FinancialYearController {

    private final FinancialYearService financialYearService;

    @GetMapping("/current")
    public SessionDTO fetchCurrentFinancialYear() {
        return financialYearService.fetchCurrentSession().get();
    }

    @GetMapping("/all")
    public List<SessionDTO> fetchAllFinancialYear() {
        return financialYearService.fetchAllFinancialYear();
    }
}
