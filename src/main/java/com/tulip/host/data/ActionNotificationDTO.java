package com.tulip.host.data;

import java.time.LocalDateTime;

import com.tulip.host.enums.NotificationStatus;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActionNotificationDTO {
    Long id;
    String entityType;
    Long entityId;
    String machineId;
    String currentState;
    String requestedUser;
    NotificationStatus status;
    LocalDateTime createdDate;
}
