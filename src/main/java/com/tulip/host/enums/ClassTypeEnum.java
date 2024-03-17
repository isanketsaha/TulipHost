package com.tulip.host.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ClassTypeEnum {
    PRENURSERY(-1),
    NURSERY(0),
    LKG(1),
    UKG(2),
    I(3),
    II(4),
    III(5),
    IV(6),
    V(7),
    VI(8),
    VII(9),
    VIII(10),
    IX(11),
    X(12);

    private final int order;

    ClassTypeEnum(int order) {
        this.order = order;
    }
}
