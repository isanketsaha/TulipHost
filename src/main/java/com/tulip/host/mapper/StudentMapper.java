package com.tulip.host.mapper;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.data.StudentExportDTO;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.service.UploadService;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.OnboardingVM;
import com.tulip.host.web.rest.vm.StudentLoadVm;
import com.tulip.host.web.rest.vm.UserEditVM;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    uses = { DependentMapper.class, ReferenceMapper.class, CommonUtils.class, UploadMapper.class, StudentToTransportMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
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
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "aadhaarCard", expression = "java(new ArrayList<>())")
    @Mapping(target = "panCard", expression = "java(new ArrayList<>())")
    @Mapping(target = "birthCertificate", expression = "java(new ArrayList<>())")
    StudentDetailsDTO toDetailEntity(Student student, @Context UploadService service);

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
    @Mapping(target = "pendingFees", ignore = true)
    @Mapping(target = "annualPaidFees", ignore = true)
    StudentBasicDTO toBasicEntity(Student student);

    @Mapping(target = "name", expression = "java(org.apache.commons.lang.WordUtils.capitalizeFully(studentLoadVms.getName()))")
    Student toModel(StudentLoadVm studentLoadVms);

    List<Student> toModelList(List<StudentLoadVm> studentLoadVms);

    @Mapping(target = "classDetails", ignore = true)
    @Mapping(target = "phoneNumber", source = "contact")
    @Mapping(target = "dependents", ignore = true)
    @Mapping(
        target = "bloodGroup",
        expression = "java(editVM.getBloodGroup() != null ? editVM.getBloodGroup().getDisplayType() : student.getBloodGroup())"
    )
    @Mapping(
        target = "name",
        expression = "java(editVM.getName() != null ? org.apache.commons.lang.WordUtils.capitalizeFully(editVM.getName()): student.getName())"
    )
    void toUpdateModel(UserEditVM editVM, @MappingTarget Student student);

    @Mapping(target = "studentId", source = "id")
    @Mapping(target = "birthday", source = "dob")
    @Mapping(
        target = "std",
        expression = "java(student.getClassDetails()\n" +
        "                .stream()\n" +
        "                .findFirst()\n" +
        "                .orElse(null)\n" +
        "                .getStd())"
    )
    @Mapping(target = "pendingFees", ignore = true)
    StudentExportDTO toBasicEntityExport(Student student);

    List<StudentExportDTO> toBasicEntityExportList(List<Student> student);

    @AfterMapping
    default void map(@MappingTarget StudentDetailsDTO target, Student source, @Context UploadService service) {
        target.setProfilePictureUrl(
            source.getProfilePicture() != null ? service.getURL(source.getProfilePicture().getUid()) : StringUtils.EMPTY
        );
    }
}
