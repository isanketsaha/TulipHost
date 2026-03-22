package com.tulip.host.service;

import static com.tulip.host.security.SecurityUtils.extractRoles;
import static com.tulip.host.security.SecurityUtils.getCurrentUserLogin;

import com.tulip.host.data.ActionNotificationDTO;
import com.tulip.host.domain.ActionNotification;
import com.tulip.host.enums.NotificationStatus;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.mapper.ActionNotificationMapper;
import com.tulip.host.repository.ActionNotificationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActionNotificationService {

    private final ActionNotificationRepository actionNotificationRepository;

    private final ActionNotificationMapper actionNotificationMapper;

    @Transactional
    public ActionNotification create(
        String entityType,
        Long entityId,
        String machineId,
        String currentState,
        String requiredEvent,
        UserRoleEnum requiredRole,
        Long requiredUserId,
        String requestedUser
    ) {
        ActionNotification notification = actionNotificationMapper.toEntity(
            entityType,
            entityId,
            machineId,
            currentState,
            requiredEvent,
            requiredRole,
            requiredUserId,
            requestedUser
        );
        return actionNotificationRepository.save(notification);
    }

    @Transactional
    public void markAction(String machineId) {
        List<ActionNotification> pending = actionNotificationRepository.findByMachineIdAndStatus(machineId, NotificationStatus.PENDING);
        pending.forEach(item -> item.setStatus(NotificationStatus.DONE));
        if (!pending.isEmpty()) {
            actionNotificationRepository.saveAll(pending);
        }
    }

    /**
     * Returns the entity IDs (for a given entityType) that have a PENDING
     * ActionNotification targeted at the current user — either by their specific
     * userId or by their role.
     *
     * This is the generic "what needs my attention?" query used by every approval
     * workflow (Leave, InventoryRequest, …).  Each feature calls this method with
     * its own entityType constant and then loads only those entity records.
     *
     * Benefit: a principal who submitted their own leave/request does NOT see it
     * in their approval queue, because the notification for that item was routed
     * to ADMIN — not to PRINCIPAL.
     */
    @Transactional(readOnly = true)
    public List<Long> getApprovalQueueEntityIds(String entityType) {
        Optional<String> userId = getCurrentUserLogin();
        List<UserRoleEnum> roles = extractRoles(SecurityContextHolder.getContext().getAuthentication());

        List<ActionNotification> results = new ArrayList<>();
        if (userId.isPresent()) {
            results.addAll(
                actionNotificationRepository.findByStatusAndEntityTypeAndApproverUserIdOrderByCreatedDateDesc(
                    NotificationStatus.PENDING,
                    entityType,
                    userId.get()
                )
            );
        }
        if (!roles.isEmpty()) {
            results.addAll(
                actionNotificationRepository.findByStatusAndEntityTypeAndApproverRoleInOrderByCreatedDateDesc(
                    NotificationStatus.PENDING,
                    entityType,
                    roles
                )
            );
        }
        return results.stream().map(ActionNotification::getEntityId).distinct().collect(Collectors.toList());
    }

    /**
     * Deletes all ActionNotifications for the given entity.
     * Called when a requester withdraws their own pending request.
     */
    @Transactional
    public void deleteByEntityTypeAndEntityId(String entityType, Long entityId) {
        List<ActionNotification> notifications = actionNotificationRepository.findByEntityTypeAndEntityId(entityType, entityId);
        if (!notifications.isEmpty()) {
            actionNotificationRepository.deleteAll(notifications);
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
                    requiredUserId.orElse("")
                )
            );
        }

        if (!roles.isEmpty()) {
            results.addAll(
                actionNotificationRepository.findByStatusAndApproverRoleInOrderByCreatedDateDesc(NotificationStatus.PENDING, roles)
            );
        }

        return results.stream().map(actionNotificationMapper::toDto).collect(Collectors.toList());
    }
}
