package com.tulip.host.mapper;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.utils.CommonUtils;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ClassMapper.class, DependentMapper.class, ReferenceMapper.class, CommonUtils.class })
public interface StudentMapper {
    @Mapping(target = "classDetails", source = "std")
    @Mapping(target = "admissionDate", source = "createdDate")
    @Mapping(target = "age", expression = "java(com.tulip.host.utils.CommonUtils.calculateAge(student.getDob()))")
    StudentDetailsDTO getEntityFromModel(Student student);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "std", source = "std.std")
    @Mapping(target = "age", expression = "java(com.tulip.host.utils.CommonUtils.calculateAge(student.getDob()))")
    StudentBasicDTO getBasicEntityFromModel(Student student);

    Student toEntity(Long id);

    List<StudentBasicDTO> getBasicEntitySetFromModelList(Set<Student> student);
    List<StudentBasicDTO> getBasicEntityListFromModelList(List<Student> student);
    //    Student getModelFromEntity(OnboardingVM source);
}
