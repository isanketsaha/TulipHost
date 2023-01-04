package com.tulip.host.mapper;

import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = { ClassMapper.class, DependentMapper.class })
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "classDetails", source = "std")
    StudentDetailsDTO getEntityFromModel(Student student);
}
