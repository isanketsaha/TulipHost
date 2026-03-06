package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseReviewVM {

    private String observations;

    @NotNull
    private List<ParameterScoreVM> scores;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParameterScoreVM {

        @NotNull
        private Long parameterId;

        @NotNull
        @Min(1)
        @Max(5)
        private Integer score;
    }
}
