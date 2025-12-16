package com.tulip.host.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tulip.host.domain.ActionNotification;
import com.tulip.host.enums.NotificationStatus;
import com.tulip.host.enums.UserRoleEnum;

@Repository
public interface ActionNotificationRepository extends JpaRepository<ActionNotification, Long> {

        List<ActionNotification> findByStatusAndApproverRoleInOrderByCreatedDateDesc(NotificationStatus status,
                        Collection<UserRoleEnum> roles);

        List<ActionNotification> findByStatusAndApproverUserIdOrderByCreatedDateDesc(NotificationStatus status,
                        String requiredUserId);

        List<ActionNotification> findByMachineIdAndStatus(String machineId, NotificationStatus status);

        List<ActionNotification> findByEntityTypeAndEntityId(String entityType, Long entityId);
}
