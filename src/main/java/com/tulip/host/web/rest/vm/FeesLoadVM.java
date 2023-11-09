package com.tulip.host.web.rest.vm;

import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

@Data
public class FeesLoadVM {

    private String label;
    private Double price;
    private String rule;
    private String classDetail;
}
