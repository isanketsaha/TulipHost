package com.tulip.host.mapper;

import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.web.rest.vm.OnboardingVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { DependentMapper.class, BankMapper.class, InterviewMapper.class })
public interface EmployeeMapper {
    @Mapping(target = "authority", source = "group.authority")
    EmployeeDetailsDTO getEntityFromModel(Employee source);

    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "interview", ignore = true)
    Employee toModel(OnboardingVM source);
}
