package com.tulip.host.service;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.data.AuditDTO;
import com.tulip.host.domain.Audit;
import com.tulip.host.domain.QAudit;
import com.tulip.host.repository.AuditRepository;
import com.tulip.host.utils.CommonUtils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    public Page<AuditDTO> fetchAudit(int pageNo, int pageSize) {
        Instant thisWeek = LocalDate.now().minus(7, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BooleanBuilder builder = new BooleanBuilder().and(QAudit.audit.createdDate.after(thisWeek));
        Page<Audit> audits = auditRepository.findAll(
            builder,
            CommonUtils.getPageRequest(Sort.Direction.DESC, "createdDate", pageNo, pageSize)
        );

        List<AuditDTO> auditDTOList = audits
            .getContent()
            .stream()
            .map(item ->
                AuditDTO
                    .builder()
                    .dateTime(item.getCreatedDate())
                    .type(item.getType())
                    .status(item.getMetadata().split(" - ")[0])
                    .endpoint(item.getMetadata().split(" - ")[1])
                    .build()
            )
            .collect(Collectors.toList());

        return new PageImpl<>(auditDTOList, audits.getPageable(), audits.getTotalElements());
    }
}
