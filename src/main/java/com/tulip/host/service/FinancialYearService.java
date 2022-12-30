package com.tulip.host.service;

import com.tulip.host.data.DropDownOptionsDto;
import com.tulip.host.repository.SessionRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialYearService {

    private final SessionRepository sessionRepository;

    public Optional<DropDownOptionsDto> fetchCurrentSession() {
        return sessionRepository
            .fetchCurrentSession()
            .map(financialYear ->
                DropDownOptionsDto.builder().label(financialYear.getDisplayText()).value(String.valueOf(financialYear.getId())).build()
            );
    }

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
