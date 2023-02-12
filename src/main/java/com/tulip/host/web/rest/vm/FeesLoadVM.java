package com.tulip.host.web.rest.vm;

import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

@Data
public class FeesLoadVM {

    @FieldName("Fees Name")
    private String feesName;

    @FieldName("Price")
    private Double price;

    @FieldName("Applicable Rule")
    private String applicableRule;

    @FieldName("Class")
    private String classDetail;

    @FieldName("Session")
    private Long session;
}
