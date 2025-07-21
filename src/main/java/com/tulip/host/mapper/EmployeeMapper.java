package com.tulip.host.mapper;

import static com.tulip.host.config.Constants.NOT_AVAILABLE;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.data.JoiningLetterDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Dependent;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.Student;
import com.tulip.host.service.UploadService;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring",
    uses = { DependentMapper.class, BankMapper.class, InterviewMapper.class, CredentialMapper.class, UploadMapper.class }
)
public interface EmployeeMapper {
    @Mapping(target = "authority", source = "group.authority")
    @Mapping(target = "dependent", source = "dependents")
    @Mapping(target = "userName", source = "credential.userId")
    EmployeeDetailsDTO toEntity(Employee source, @Context UploadService service);

    @Mapping(target = "phoneNumber", source = "contact")
    @Mapping(target = "dependents", source = "dependent")
    @Mapping(target = "bloodGroup", expression = "java(source.getBloodGroup().getDisplayType())")
    @Mapping(target = "name", expression = "java(org.apache.commons.lang3.text.WordUtils.capitalizeFully(source.getName()))")
    @Mapping(target = "tid", source = "tid")
    Employee toModel(OnboardingVM source);

    @Mapping(target = "age", expression = "java(com.tulip.host.utils.CommonUtils.calculateAge(source.getDob()))")
    @Mapping(target = "classTeacher", source = ".", qualifiedByName = "findClassTeacher")
    @Mapping(target = "authority", source = "group.authority")
    EmployeeBasicDTO toBasicEntity(Employee source);

    List<EmployeeDetailsDTO> toEntityList(List<Employee> source);

    List<EmployeeBasicDTO> toBasicEntityList(List<Employee> source);

    @Named("findClassTeacher")
    default String getClassTeacher(Employee source) {
        ClassDetail classDetail = source.getClassDetails().stream().findFirst().orElse(null);
        return classDetail != null ? classDetail.getStd() : NOT_AVAILABLE;
    }

    @Mapping(target = "name", expression = "java(org.apache.commons.lang3.text.WordUtils.capitalizeFully(source.getName()))")
    @Mapping(target = "position", source = "group.authority")
    @Mapping(target = "salary", source = "interview.salary")
    @Mapping(target = "doj", source = "interview.doj", dateFormat = "dd-MM-yyyy")
    @Mapping(target = "bankName", source = "bank.bankName")
    @Mapping(target = "accountNo", source = "bank.accountNo")
    //    @Mapping(target = "aadharNo", source = "bank.bankName")
    @Mapping(target = "highestQualification", source = "qualification")
    @Mapping(target = "dependentName", ignore = true)
    @Mapping(target = "dependentAadhar", ignore = true)
    @Mapping(target = "relation", ignore = true)
    JoiningLetterDTO toPrintEntity(Employee source);

    @AfterMapping
    default void map(@MappingTarget JoiningLetterDTO target, Employee source) {
        Dependent dependent = source.getDependents().stream().findFirst().orElse(null);
        if (dependent != null) {
            target.setDependentName(dependent.getName());
            target.setDependentAadhar(dependent.getAadhaarNo());
            target.setRelation(dependent.getRelationship());
        }
    }

    @AfterMapping
    default void map(@MappingTarget EmployeeDetailsDTO target, Employee source, @Context UploadService service) {
        target.setProfilePictureUrl(
            source.getProfilePicture() != null ? service.getURL(source.getProfilePicture().getUid()) : StringUtils.EMPTY
        );
    }
}
