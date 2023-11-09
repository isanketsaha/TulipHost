package com.tulip.host.service;

import com.tulip.host.data.SessionDTO;
import com.tulip.host.domain.Session;
import com.tulip.host.mapper.SessionMapper;
import com.tulip.host.repository.SessionRepository;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    private final SessionMapper sessionMapper;

    public SessionDTO fetchCurrentSession() {
        return sessionMapper.toDto(sessionRepository.fetchCurrentSession());
    }

    public SessionDTO fetchSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            sessionMapper.toDto(session);
        }
        return null;
    }

    public Session currentSession() {
        return sessionRepository.fetchCurrentSession();
    }

    public Session sessionByDate(Date date) {
        return sessionRepository.sessionByDate(date);
    }
}
