package com.tulip.host.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum BloodGroup {
    aPositive("A+"),
    aNegative("A-"),
    bPositive("B+"),
    bNegative("B-"),
    oPositive("O+"),
    oNegative("O-"),
    abPositive("AB+"),
    abNegative("AB-");

    private final String displayType;

    BloodGroup(String displayType) {
        this.displayType = displayType;
    }
}
