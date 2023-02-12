package com.tulip.host.mapper;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.OnboardingVM;
import com.tulip.host.web.rest.vm.StudentLoadVm;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { DependentMapper.class, ReferenceMapper.class, CommonUtils.class })
public interface StudentMapper {
    @Mapping(target = "classDetails", ignore = true)
    @Mapping(target = "phoneNumber", source = "contact")
    @Mapping(target = "dependents", source = "dependent")
    @Mapping(target = "bloodGroup", expression = "java(source.getBloodGroup().getDisplayType())")
    @Mapping(target = "name", expression = "java(org.apache.commons.lang.WordUtils.capitalizeFully(source.getName()))")
    Student toModel(OnboardingVM source);

    Student toEntity(Long id);

    List<StudentBasicDTO> toBasicEntityList(List<Student> student);

    List<StudentBasicDTO> toBasicEntityList(Set<Student> student);

    @Mapping(target = "admissionDate", source = "createdDate")
    @Mapping(target = "age", expression = "java(com.tulip.host.utils.CommonUtils.calculateAge(student.getDob()))")
    @Mapping(target = "dependent", source = "dependents")
    @Mapping(target = "classDetails", ignore = true)
    StudentDetailsDTO toDetailEntity(Student student);

    @Mapping(
        target = "classId",
        expression = "java(student.getClassDetails()\n" +
        "                .stream()\n" +
        "                .findFirst()\n" +
        "                .orElse(null)\n" +
        "                .getId())"
    )
    @Mapping(
        target = "std",
        expression = "java(student.getClassDetails()\n" +
        "                .stream()\n" +
        "                .findFirst()\n" +
        "                .orElse(null)\n" +
        "                .getStd())"
    )
    @Mapping(target = "age", expression = "java(com.tulip.host.utils.CommonUtils.calculateAge(student.getDob()))")
    StudentBasicDTO toBasicEntity(Student student);

    @Mapping(target = "dob", dateFormat = "MM/dd/yyyy")
    @Mapping(target = "name", expression = "java(org.apache.commons.lang.WordUtils.capitalizeFully(studentLoadVms.getName()))")
    Student toModel(StudentLoadVm studentLoadVms);

    List<Student> toModelList(List<StudentLoadVm> studentLoadVms);
}
