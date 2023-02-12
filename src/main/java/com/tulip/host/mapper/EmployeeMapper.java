package com.tulip.host.mapper;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { DependentMapper.class, BankMapper.class, InterviewMapper.class, CredentialMapper.class })
public interface EmployeeMapper {
    @Mapping(target = "authority", source = "group.authority")
    @Mapping(target = "dependent", source = "dependents")
    EmployeeDetailsDTO toEntity(Employee source);

    @Mapping(target = "phoneNumber", source = "contact")
    @Mapping(target = "dependents", source = "dependent")
    @Mapping(target = "bloodGroup", expression = "java(source.getBloodGroup().getDisplayType())")
    Employee toModel(OnboardingVM source);

    @Mapping(target = "age", expression = "java(com.tulip.host.utils.CommonUtils.calculateAge(source.getDob()))")
    EmployeeBasicDTO toBasicEntity(Employee source);

    List<EmployeeDetailsDTO> toEntityList(List<Employee> source);

    List<EmployeeBasicDTO> toBasicEntityList(List<Employee> source);
}
