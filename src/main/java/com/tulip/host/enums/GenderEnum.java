package com.tulip.host.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum GenderEnum {
    MALE("Male"),
    FEMALE("Female");

    private final String displayType;

    GenderEnum(String displayType) {
        this.displayType = displayType;
    }
}
