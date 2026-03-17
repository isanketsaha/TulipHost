package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelfAssessmentVM {

    @NotNull
    private Long reviewId;

    private String selfAssessment;

    private List<String> attachmentUuids;
}
