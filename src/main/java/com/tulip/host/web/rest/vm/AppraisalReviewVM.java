package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppraisalReviewVM {

    private Long appraisalId;

    @NotNull
    private String reviewType;
}
