package com.tulip.host.web.rest.vm.dataload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tulip.host.web.rest.vm.ClassLoadVM;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class SessionLoadVM {

    @JsonProperty("label")
    private String displayText;

    @JsonProperty("startDate")
    private Date fromDate;

    @JsonProperty("endDate")
    private Date toDate;

    List<ClassLoadVM> stdList;

    Map<String, List<FeesLoadVM>> feesList;
}
