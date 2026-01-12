package com.tulip.host.web.rest;

import com.tulip.host.service.ScheduledService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('UG_ADMIN') or hasAuthority('UG_PRINCIPAL')")
public class ScheduledController {

    private final ScheduledService scheduledService;

    @GetMapping("/notifyFeesDues")
    public void notifyOnFeesDues() {
        this.scheduledService.notifyOnFeesDues();
    }

}
