package com.tulip.host.web.rest;

import com.tulip.host.service.VisualizeService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visualize")
@RequiredArgsConstructor
@Slf4j
public class VisualizeController {

    private final VisualizeService visualizeService;

    @GetMapping("/admission")
    public Map<String, Long> admissionByMonth() {
        return visualizeService.admissionByMonth();
    }

    @GetMapping("/expense")
    public Map<String, Map<String, Double>> getExpenseRecord() {
        return visualizeService.getExpenseRecord();
    }

    @GetMapping("/mrr")
    public double getMRR() {
        return visualizeService.getMRR();
    }

    @GetMapping("/yrr")
    public double getYRR() {
        return visualizeService.getYRR();
    }
}
