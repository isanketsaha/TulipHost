package com.tulip.host.repository;

import com.tulip.host.domain.ActionNotification;
import com.tulip.host.enums.NotificationStatus;
import com.tulip.host.enums.UserRoleEnum;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionNotificationRepository extends JpaRepository<ActionNotification, Long> {
    List<ActionNotification> findByStatusAndApproverRoleInOrderByCreatedDateDesc(NotificationStatus status, Collection<UserRoleEnum> roles);

    List<ActionNotification> findByStatusAndApproverUserIdOrderByCreatedDateDesc(NotificationStatus status, String requiredUserId);

    List<ActionNotification> findByMachineIdAndStatus(String machineId, NotificationStatus status);

    List<ActionNotification> findByEntityTypeAndEntityId(String entityType, Long entityId);

    // ── Approval-queue queries scoped to a specific entity type ──────────────
    // Used by ActionNotificationService.getApprovalQueueEntityIds() so that
    // each feature loads only the items pending for the current user's role,
    // rather than all items of a given status.

    List<ActionNotification> findByStatusAndEntityTypeAndApproverRoleInOrderByCreatedDateDesc(
        NotificationStatus status,
        String entityType,
        Collection<UserRoleEnum> roles
    );

    List<ActionNotification> findByStatusAndEntityTypeAndApproverUserIdOrderByCreatedDateDesc(
        NotificationStatus status,
        String entityType,
        String approverUserId
    );
}
