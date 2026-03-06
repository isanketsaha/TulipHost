package com.tulip.host.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppraisalReviewDTO implements Serializable {

    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reviewType;

    private String reviewStatus;

    private String observations;

    private String selfAssessment;

    private String selfAssessmentAttachments;

    private Double rating;

    private List<ReviewParameterScoreDTO> scores = new ArrayList<>();
}
