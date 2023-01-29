package com.tulip.host.web.rest.vm;

import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

@Data
public class FeesLoadVM {

    @FieldName("FeesName")
    private String feesName;

    @FieldName("Price")
    private Double price;

    @FieldName("applicableRule")
    private String applicableRule;

    @FieldName("Class")
    private Long std;
}
