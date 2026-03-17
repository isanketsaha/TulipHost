package com.tulip.host.mapper;

import com.tulip.host.data.ClassSubjectDTO;
import com.tulip.host.data.ParameterDTO;
import com.tulip.host.domain.AssessmentParameter;
import com.tulip.host.domain.ClassSubject;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CurriculumMapper {
    ParameterDTO toDTO(AssessmentParameter param);

    List<ParameterDTO> toAssessmentDTOList(List<AssessmentParameter> params);

    @Mapping(target = "assessmentParams", source = "assessmentParams")
    @Mapping(target = "books", ignore = true)
    ClassSubjectDTO toDTO(ClassSubject classSubject);

    List<ClassSubjectDTO> toDTOList(List<ClassSubject> classSubjects);

    default List<ParameterDTO> fromAssessmentSet(Set<AssessmentParameter> set) {
        if (set == null) return List.of();
        return set.stream().map(this::toDTO).toList();
    }
}
