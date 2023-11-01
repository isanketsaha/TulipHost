package com.tulip.host.mapper;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.data.JoiningLetterDTO;
import com.tulip.host.domain.Dependent;
import com.tulip.host.domain.Employee;
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

@Mapper(componentModel = "spring", uses = { DependentMapper.class, BankMapper.class, InterviewMapper.class, CredentialMapper.class })
public interface EmployeeMapper {
    @Mapping(target = "authority", source = "group.authority")
    @Mapping(target = "dependent", source = "dependents")
    EmployeeDetailsDTO toEntity(Employee source);

    @Mapping(target = "phoneNumber", source = "contact")
    @Mapping(target = "dependents", source = "dependent")
    @Mapping(target = "bloodGroup", expression = "java(source.getBloodGroup().getDisplayType())")
    @Mapping(target = "name", expression = "java(org.apache.commons.lang.WordUtils.capitalizeFully(source.getName()))")
    @Mapping(target = "profilePicture", ignore = true)
    Employee toModel(OnboardingVM source, @Context UploadService uploadService);

    @Mapping(target = "age", expression = "java(com.tulip.host.utils.CommonUtils.calculateAge(source.getDob()))")
    @Mapping(target = "classTeacher", source = ".", qualifiedByName = "findClassTeacher")
    EmployeeBasicDTO toBasicEntity(Employee source);

    List<EmployeeDetailsDTO> toEntityList(List<Employee> source);

    List<EmployeeBasicDTO> toBasicEntityList(List<Employee> source);

    @Named("findClassTeacher")
    default String getClassTeacher(Employee source) {
        return CollectionUtils.isEmpty(source.getClassDetails()) ? "" : source.getClassDetails().stream().findFirst().get().getStd();
    }

    @AfterMapping
    default void map(@MappingTarget Employee target, OnboardingVM source, @Context UploadService service) {
        target.setProfilePicture(
            source.getProfilePicture() != null ? service.getURL(source.getProfilePicture().getUid()) : StringUtils.EMPTY
        );
    }

    @Mapping(target = "name", expression = "java(org.apache.commons.lang.WordUtils.capitalizeFully(source.getName()))")
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
}
