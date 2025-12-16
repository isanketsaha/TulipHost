package com.tulip.host.domain;

import com.tulip.host.enums.NotificationStatus;
import com.tulip.host.enums.UserRoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "action_notification")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ActionNotification extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", length = 40, nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "machine_id", length = 120, nullable = false)
    private String machineId;

    @Column(name = "current_state", length = 40, nullable = false)
    private String currentState;

    @Enumerated(EnumType.STRING)
    @Column(name = "approver_role", length = 30, nullable = false)
    private UserRoleEnum approverRole;

    @Column(name = "approver_user_id")
    private String approverUserId;

    @Column(name = "requested_user", length = 100)
    private String requestedUser;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15, nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Override
    public Long getId() {
        return id;
    }
}
