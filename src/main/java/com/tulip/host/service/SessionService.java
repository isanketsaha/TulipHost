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
        return sessionRepository.fetchCurrentSession().orElse(null);
    }

    public SessionDTO fetchSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            return SessionDTO.builder().displayText(session.getDisplayText()).id(sessionId).build();
        }
        return null;
    }
}
