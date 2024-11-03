package com.tulip.host.service;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.data.AuditDTO;
import com.tulip.host.domain.Audit;
import com.tulip.host.domain.QAudit;
import com.tulip.host.repository.AuditRepository;
import com.tulip.host.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    @Transactional
    public Page<AuditDTO> fetchAudit(int pageNo, int pageSize) {
        BooleanBuilder builder = new BooleanBuilder()
            .and(QAudit.audit.createdDate.after(LocalDateTime.now().minusDays(15)))
            .and(QAudit.audit.resolved.eq(false));
        Page<Audit> audits = auditRepository.findAll(
            builder,
            CommonUtils.getPageRequest(Sort.Direction.DESC, "createdDate", pageNo, pageSize)
        );

        List<AuditDTO> auditDTOList = audits
            .getContent()
            .stream()
            .map(item -> {
                String[] split = item.getMetadata().split(" - ");
                return AuditDTO
                    .builder()
                    .id(item.getId())
                    .dateTime(item.getCreatedDate())
                    .type(item.getType())
                    .description(item.getDescription())
                    .status(split.length == 2 ? split[0] : null)
                    .endpoint(split.length == 2 ? split[1] : split[0])
                    .build();
            })
            .collect(Collectors.toList());

        return new PageImpl<>(auditDTOList, audits.getPageable(), audits.getTotalElements());
    }

    public void auditResolved(long auditId) {
        int i = auditRepository.updateResolvedById(auditId);
    }
}
