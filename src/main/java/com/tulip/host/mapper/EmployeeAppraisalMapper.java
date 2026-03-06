package com.tulip.host.mapper;

import com.tulip.host.data.AppraisalParameterDTO;
import com.tulip.host.data.AppraisalReviewDTO;
import com.tulip.host.data.EmployeeAppraisalDTO;
import com.tulip.host.data.ReviewParameterScoreDTO;
import com.tulip.host.domain.AppraisalReview;
import com.tulip.host.domain.AppraisalSelectedParameter;
import com.tulip.host.domain.EmployeeAppraisal;
import com.tulip.host.domain.ReviewParameterScore;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeAppraisalMapper {
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "sessionDisplayText", source = "session.displayText")
    @Mapping(target = "selectedParameters", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    EmployeeAppraisalDTO toDto(EmployeeAppraisal source);

    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "sessionDisplayText", source = "session.displayText")
    @Mapping(target = "selectedParameters", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    List<EmployeeAppraisalDTO> toDtoList(List<EmployeeAppraisal> source);

    // scores list is auto-mapped using toScoreDto below
    AppraisalReviewDTO toReviewDto(AppraisalReview source);

    List<AppraisalReviewDTO> toReviewDtoList(List<AppraisalReview> source);

    @Mapping(target = "parameterId", source = "parameter.id")
    @Mapping(target = "parameterName", source = "parameter.name")
    ReviewParameterScoreDTO toScoreDto(ReviewParameterScore source);

    @Mapping(target = "id", source = "parameter.id")
    @Mapping(target = "name", source = "parameter.name")
    AppraisalParameterDTO toParameterDto(AppraisalSelectedParameter source);

    List<AppraisalParameterDTO> toParameterDtoList(List<AppraisalSelectedParameter> source);
}
