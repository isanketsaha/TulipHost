package com.tulip.host.service;

import com.tulip.host.data.DropDownOptionsDto;
import com.tulip.host.data.SessionDTO;
import com.tulip.host.domain.Session;
import com.tulip.host.repository.SessionRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialYearService {

    private final SessionRepository sessionRepository;

    public DropDownOptionsDto fetchCurrentSession() {
        Session sessionDTO = sessionRepository.fetchCurrentSession();
        if (sessionDTO != null) {
            return DropDownOptionsDto.builder().label(sessionDTO.getDisplayText()).value(String.valueOf(sessionDTO.getId())).build();
        }
        return null;
    }

    @Transactional
    public List<DropDownOptionsDto> fetchAllFinancialYear() {
        return sessionRepository
            .listAllFinancialYears()
            .stream()
            .map(financialYear ->
                DropDownOptionsDto.builder().label(financialYear.getDisplayText()).value(String.valueOf(financialYear.getId())).build()
            )
            .collect(Collectors.toList());
    }
}
