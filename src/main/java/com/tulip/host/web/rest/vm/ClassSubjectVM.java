package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.ExamType;
import com.tulip.host.enums.TermMarkType;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class ClassSubjectVM {

    @NotBlank
    private String subjectKey;

    private String displayName;

    private boolean included;

    private Set<ExamType> examTypes;

    /** Required when examTypes contains TERM. FULL = 80 ext marks; HALF = 50 ext marks, no internal. */
    private TermMarkType termMarkType;

    private List<Long> assessmentParamIds;

    private List<Long> bookProductIds;
}
