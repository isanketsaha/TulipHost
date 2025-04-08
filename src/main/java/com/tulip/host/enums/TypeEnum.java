package com.tulip.host.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum TypeEnum {

    ACADEMICS("GREEN"), EVENT("Orange"), HOLIDAY("Red");

    private final String color;

    TypeEnum(String color) {
        this.color = color;
    }

}
