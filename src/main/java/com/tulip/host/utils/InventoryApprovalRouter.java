package com.tulip.host.utils;

import com.tulip.host.enums.UserRoleEnum;
import org.springframework.stereotype.Component;

/**
 * Central rule: given a requester's role, returns the role that must approve.
 *
 *   TEACHER / STAFF  →  PRINCIPAL
 *   PRINCIPAL        →  ADMIN
 *   ADMIN            →  ADMIN   (admin raising their own request → admin approves)
 */
@Component
public class InventoryApprovalRouter {

    public UserRoleEnum getApproverFor(UserRoleEnum requesterRole) {
        return switch (requesterRole) {
            case TEACHER, STAFF -> UserRoleEnum.PRINCIPAL;
            case PRINCIPAL -> UserRoleEnum.ADMIN;
            default -> UserRoleEnum.ADMIN;
        };
    }
}
