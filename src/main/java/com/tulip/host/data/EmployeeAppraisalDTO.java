package com.tulip.host.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAppraisalDTO implements Serializable {

    private Long id;

    private Long sessionId;

    private String sessionDisplayText;

    private Integer newSalary;

    private Double finalRating;

    private String status;

    private List<AppraisalParameterDTO> selectedParameters = new ArrayList<>();

    private List<AppraisalReviewDTO> reviews = new ArrayList<>();
}
