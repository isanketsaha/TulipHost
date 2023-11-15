package com.tulip.host.web.rest.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClassLoadVM {

    @JsonProperty("label")
    String std;

    @JsonProperty("classTeacher")
    Long classTeacher;
}
