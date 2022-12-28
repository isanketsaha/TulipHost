package com.tulip.host.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private final String displayType;

    Gender(String displayType) {
        this.displayType = displayType;
    }
}
