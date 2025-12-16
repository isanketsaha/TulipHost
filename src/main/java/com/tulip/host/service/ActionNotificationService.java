package com.tulip.host.service;

import static com.tulip.host.security.SecurityUtils.extractRoles;
import static com.tulip.host.security.SecurityUtils.getCurrentUserLogin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tulip.host.data.ActionNotificationDTO;
import com.tulip.host.domain.ActionNotification;
import com.tulip.host.enums.NotificationStatus;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.mapper.ActionNotificationMapper;
import com.tulip.host.repository.ActionNotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActionNotificationService {

    private final ActionNotificationRepository actionNotificationRepository;

    private final ActionNotificationMapper actionNotificationMapper;

    @Transactional
    public ActionNotification create(String entityType, Long entityId, String machineId, String currentState,
            String requiredEvent, UserRoleEnum requiredRole, Long requiredUserId, String requestedUser) {
        ActionNotification notification = actionNotificationMapper.toEntity(entityType, entityId, machineId,
                currentState, requiredEvent, requiredRole, requiredUserId, requestedUser);
        return actionNotificationRepository.save(notification);
    }

    @Transactional
    public void markAction(String machineId) {
        List<ActionNotification> pending = actionNotificationRepository.findByMachineIdAndStatus(machineId,
                NotificationStatus.PENDING);
        pending.forEach(item -> item.setStatus(NotificationStatus.DONE));
        if (!pending.isEmpty()) {
            actionNotificationRepository.saveAll(pending);
        }
    }

    @Transactional(readOnly = true)
    public List<ActionNotificationDTO> fetchNotification() {

        Optional<String> requiredUserId = getCurrentUserLogin();
        List<UserRoleEnum> roles = extractRoles(SecurityContextHolder.getContext().getAuthentication());

        List<ActionNotification> results = new ArrayList<>();
        if (requiredUserId.isPresent()) {
            results.addAll(
                    actionNotificationRepository.findByStatusAndApproverUserIdOrderByCreatedDateDesc(
                            NotificationStatus.PENDING,
                            requiredUserId.orElse("")));
        }

        if (!roles.isEmpty()) {
            results.addAll(
                    actionNotificationRepository.findByStatusAndApproverRoleInOrderByCreatedDateDesc(
                            NotificationStatus.PENDING,
                            roles));
        }

        return results
                .stream()
                .map(actionNotificationMapper::toDto)
                .collect(Collectors.toList());
    }
}
