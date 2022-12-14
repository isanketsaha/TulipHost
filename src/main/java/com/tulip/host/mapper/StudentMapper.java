package com.tulip.host.mapper;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = { ClassMapper.class, DependentMapper.class })
public interface StudentMapper {
    @Mapping(target = "classDetails", source = "std")
    @Mapping(target = "admissionDate", source = "createdDate")
    StudentDetailsDTO getEntityFromModel(Student student);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "std", source = "std.std")
    StudentBasicDTO getBasicEntityFromModel(Student student);

    List<StudentBasicDTO> getBasicEntityListFromModelList(Set<Student> student);
    //    Student getModelFromEntity(OnboardingVM source);
}
