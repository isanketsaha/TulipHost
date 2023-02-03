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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    @Transactional
    public Page<AuditDTO> fetchAudit(int pageNo, int pageSize) {
        //        Instant thisWeek = LocalDate.now().minus(7, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date thisWeek = DateUtils.addDays(new Date(), -15);
        BooleanBuilder builder = new BooleanBuilder().and(QAudit.audit.createdDate.after(thisWeek)).and(QAudit.audit.resolved.eq(false));
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
                    .id(item.getId())
                    .dateTime(item.getCreatedDate())
                    .type(item.getType())
                    .description(item.getDescription())
                    .status(item.getMetadata().split(" - ")[0])
                    .endpoint(item.getMetadata().split(" - ")[1])
                    .build()
            )
            .collect(Collectors.toList());

        return new PageImpl<>(auditDTOList, audits.getPageable(), audits.getTotalElements());
    }

    public void auditResolved(long auditId) {
        int i = auditRepository.updateResolvedById(auditId);
    }
}
