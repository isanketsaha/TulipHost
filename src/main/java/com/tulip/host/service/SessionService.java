package com.tulip.host.service;

import com.tulip.host.data.SessionDTO;
import com.tulip.host.domain.Session;
import com.tulip.host.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionDTO fetchCurrentSession() {
        Session session = sessionRepository.fetchCurrentSession();
        return SessionDTO.builder().displayText(session.getDisplayText()).id(session.getId()).build();
    }

    public SessionDTO fetchSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            return SessionDTO.builder().displayText(session.getDisplayText()).id(sessionId).build();
        }
        return null;
    }

    public Session currentSession() {
        return sessionRepository.fetchCurrentSession();
    }
}
