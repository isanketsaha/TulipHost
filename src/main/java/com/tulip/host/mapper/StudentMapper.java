package com.tulip.host.mapper;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.data.StudentExportDTO;
import com.tulip.host.data.EnrollmentLetterDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.Student;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.service.UploadService;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.OnboardingVM;
import com.tulip.host.web.rest.vm.StudentLoadVm;
import com.tulip.host.web.rest.vm.UserEditVM;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    uses = {DependentMapper.class, ReferenceMapper.class, CommonUtils.class, UploadMapper.class, StudentToTransportMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentMapper {
    @Mapping(target = "classDetails", ignore = true)
    @Mapping(target = "phoneNumber", source = "contact")
    @Mapping(target = "dependents", source = "dependent")
    @Mapping(target = "bloodGroup", expression = "java(source.getBloodGroup().getDisplayType())")
    @Mapping(target = "name", expression = "java(org.apache.commons.lang3.text.WordUtils.capitalizeFully(source.getName()))")
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

    @Mapping(target = "name", expression = "java(org.apache.commons.lang3.text.WordUtils.capitalizeFully(studentLoadVms.getName()))")
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
        expression = "java(editVM.getName() != null ? org.apache.commons.lang3.text.WordUtils.capitalizeFully(editVM.getName()): student.getName())"
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

    @Mapping(target = "studentName", source = "name")
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "dateOfBirth", source = "dob", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "className", expression = "java(source.getClassDetails().stream().findFirst().orElse(null) != null ? source.getClassDetails().stream().findFirst().get().getStd() : \"\")")
    @Mapping(target = "rollNumber", source = "id")
    @Mapping(target = "admissionDate", source = "createdDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "tuitionFee", ignore = true)
    @Mapping(target = "paymentFrequency", ignore = true)
    @Mapping(target = "parentName", ignore = true)
    @Mapping(target = "parentContact", ignore = true)
    @Mapping(target = "academicYear", ignore = true)
    EnrollmentLetterDTO toPrintEntity(Student source);

    @AfterMapping
    default void map(@MappingTarget EnrollmentLetterDTO target, Student source) {
        ClassDetail registeredStd = source.getClassDetails()
            .last();
        String displayText = registeredStd
            .getSession()
            .getDisplayText();
        String yearId = Arrays.stream(displayText.split("-"))
            .map(year -> year.substring(2))
            .collect(Collectors.joining());
        target.setAcademicYear(displayText);
        target.setStudentId("TES/" + yearId + "/" + source.getId());
        com.tulip.host.domain.Dependent parentDependent = source.getDependents()
            .stream()
            .filter(d -> d.getRelationship() != null && d.getRelationship()
                .toLowerCase()
                .contains("parent"))
            .findFirst()
            .orElse(source.getDependents()
                .stream()
                .findFirst()
                .orElse(null));

        target.setPaymentFrequency("Monthly");
        if (parentDependent != null) {
            target.setParentName(parentDependent.getName());
            target.setParentContact(parentDependent.getContact());
        }
        Set<FeesCatalog> feesCatalogs = registeredStd
            .getFeesCatalogs()
            .stream()
            .filter(fees -> fees.getActive() && fees.getApplicableRule()
                .equals(FeesRuleType.MONTHLY))
            .collect(Collectors.toSet());
        double totalFees = feesCatalogs.stream()
            .filter(fees -> !fees.getFeesName()
                .toLowerCase()
                .contains("late"))
            .mapToDouble(FeesCatalog::getPrice)
            .sum();
        target.setTuitionFee(totalFees);

    }
}
