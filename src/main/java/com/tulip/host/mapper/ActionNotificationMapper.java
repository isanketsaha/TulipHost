package com.tulip.host.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.tulip.host.data.ActionNotificationDTO;
import com.tulip.host.domain.ActionNotification;
import com.tulip.host.enums.UserRoleEnum;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActionNotificationMapper {

    ActionNotificationDTO toDto(ActionNotification notification);

    @Mapping(target = "status", constant = "PENDING")
    ActionNotification toEntity(String entityType, Long entityId, String machineId, String currentState,
            String requiredEvent, UserRoleEnum approverRole, Long approverUserId, String requestedUser);
}
