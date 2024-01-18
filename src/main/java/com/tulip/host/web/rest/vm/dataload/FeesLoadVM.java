package com.tulip.host.web.rest.vm.dataload;

import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

@Data
public class FeesLoadVM extends DataLoadVM {

    private String rule;

    private String applicableRule;
}
