package com.tulip.host.mapper;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.domain.ClassDetail;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    @Mapping(target = "headTeacher", source = "headTeacher.name")
    @Mapping(target = "session", source = "session.displayText")
    @Mapping(target = "studentStrength", expression = "java(student.getStudentList() != null ? student.getStudentList().size():0)")
    ClassDetailDTO getEntityFromModel(ClassDetail student);

    List<ClassDetailDTO> getEntityListFromModelList(List<ClassDetail> classDetails);
}
