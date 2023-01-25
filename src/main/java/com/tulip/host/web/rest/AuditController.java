package com.tulip.host.web.rest;

import com.tulip.host.data.AuditDTO;
import com.tulip.host.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('UG_ADMIN')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public Page<AuditDTO> audit(
        @RequestParam(value = "page", defaultValue = "0") int pageNo,
        @RequestParam(name = "size", defaultValue = "6") int pageSize
    ) {
        return auditService.fetchAudit(pageNo, pageSize);
    }
}
