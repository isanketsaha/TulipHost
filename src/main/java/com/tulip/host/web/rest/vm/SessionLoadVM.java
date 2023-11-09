package com.tulip.host.web.rest.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
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
