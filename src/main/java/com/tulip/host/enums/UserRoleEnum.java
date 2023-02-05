package com.tulip.host.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum UserRoleEnum {
    TEACHER("UG_TEACHER"),
    STAFF("UG_STAFF");

    private final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }
}
