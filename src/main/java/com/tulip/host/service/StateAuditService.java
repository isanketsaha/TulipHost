package com.tulip.host.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tulip.host.domain.StateAuditHistory;
import com.tulip.host.enums.LeaveEvents;
import com.tulip.host.enums.LeaveStatus;
import com.tulip.host.repository.StateAuditHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateAuditService {

    private final StateAuditHistoryRepository auditRepository;

    @Transactional
    public void recordTransition(String machineId, String fromState, String toState,
            String event, boolean success, String errorMessage) {
        try {
            StateAuditHistory audit = StateAuditHistory.builder()
                    .machineId(machineId)
                    .fromState( fromState )
                    .toState(toState)
                    .event(event)
                    .success(success)
                    .errorMessage(errorMessage)
                    .build();
            auditRepository.save(audit);
            log.debug("Audit recorded: {} -> {} (success: {})", fromState, toState, success);
        } catch (Exception e) {
            log.error("Failed to record audit trail: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void recordFailedTransition(String machineId, String currentState, String event,
            String errorMessage) {
        recordTransition(machineId, currentState, null, event, false, errorMessage);
    }

    public List<StateAuditHistory> getAuditHistoryByMachineId(String machineId) {
        return auditRepository.findByMachineIdOrderByCreatedDateDesc(machineId);
    }

    public List<StateAuditHistory> getFailedTransitions() {
        return auditRepository.findBySuccessFalseOrderByCreatedDateDesc();
    }
}
