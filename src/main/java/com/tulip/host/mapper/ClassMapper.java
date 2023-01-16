package com.tulip.host.mapper;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.domain.ClassDetail;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    @Mapping(target = "headTeacher", source = "headTeacher.name")
    @Mapping(target = "session", source = "session.displayText")
    @Mapping(target = "studentStrength", expression = "java(classDetail.getStudents() != null ? classDetail.getStudents().size():0)")
    ClassDetailDTO getEntityFromModel(ClassDetail classDetail);

    List<ClassDetailDTO> getEntityListFromModelList(List<ClassDetail> classDetails);
}
