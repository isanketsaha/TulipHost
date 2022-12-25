package com.tulip.host.service;

import com.tulip.host.data.pojo.SessionPojo;
import com.tulip.host.repository.SessionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialYearService {

    private final SessionRepository sessionRepository;

    public Optional<SessionPojo> fetchCurrentSession() {
        return sessionRepository.fetchCurrentSession().map(financialYear -> financialYear);
    }

    public List<SessionPojo> fetchAllFinancialYear() {
        return sessionRepository.listAllFinancialYears();
    }
}
