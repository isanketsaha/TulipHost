package com.tulip.host.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum UserRoleEnum {
    TEACHER("UG_TEACHER", 2),
    STAFF("UG_STAFF", 3),

    PRINCIPAL("UG_PRINCIPAL", 4),

    ADMIN("UG_ADMIN", 5);

    private final String value;
    private final int priority;

    UserRoleEnum(String value, int priority) {
        this.value = value;
        this.priority = priority;
    }
}
