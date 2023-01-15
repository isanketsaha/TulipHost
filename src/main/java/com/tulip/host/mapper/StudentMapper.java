package com.tulip.host.mapper;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ClassMapper.class, DependentMapper.class, ReferenceMapper.class, CommonUtils.class })
public interface StudentMapper {
    //    @Mapping(target = "classDetails", source = "std.std")
    @Mapping(target = "admissionDate", source = "createdDate")
    @Mapping(target = "age", expression = "java(com.tulip.host.utils.CommonUtils.calculateAge(student.getDob()))")
    StudentDetailsDTO getEntityFromModel(Student student);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "std", ignore = true)
    @Mapping(target = "age", expression = "java(com.tulip.host.utils.CommonUtils.calculateAge(student.getDob()))")
    StudentBasicDTO getBasicEntityFromModel(Student student);

    Student toEntity(Long id);

    List<StudentBasicDTO> getBasicEntitySetFromModelList(Set<Student> student);

    List<StudentBasicDTO> getBasicEntityListFromModelList(List<Student> student);

    @Mapping(target = "phoneNumber", source = "contact")
    //    @Mapping(target = "dependents", defaultValue = "dependent")
    @Mapping(target = "std", ignore = true)
    Student getModelFromEntity(OnboardingVM source);
}
