package com.tulip.host.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentBehaviourReviewDTO implements Serializable {

    private List<SubjectReview> subjects;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectReview {

        private String subjectKey;
        private Double avgScore;
        private List<WeekReview> weeks;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeekReview {

        private LocalDate weekStartDate;
        private String uploadedBy;
        private Double avgScore;
        private List<ParameterScore> scores;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParameterScore {

        private String parameterName;
        private int score;
    }
}
