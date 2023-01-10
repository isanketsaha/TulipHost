package com.tulip.host.mapper;

import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.Interview;
import com.tulip.host.web.rest.vm.InterviewVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = { DependentMapper.class, BankMapper.class, InterviewMapper.class })
public interface EmployeeMapper {
    @Mapping(target = "authority", source = "group.authority")
    EmployeeDetailsDTO getEntityFromModel(Employee source);
    //    Employee getModelFromEntity(OnboardingVM source);
}
