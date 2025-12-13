package com.tulip.host.mapper;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.tulip.host.data.EmployeeLeaveDto;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.web.rest.vm.ApplyLeaveVM;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeLeaveMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeTid", source = "employee.tid")
    @Mapping(target = "employeeName", source = "employee.name")
    @Mapping(target = "approvedBy", source = "approvedBy.name")

    EmployeeLeaveDto toDto(EmployeeLeave employeeLeave);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "leaveType", ignore = true)
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "approvalDate", expression = "java(java.time.LocalDateTime.now())")
    EmployeeLeave toEntity(ApplyLeaveVM applyLeaveVM);

    @AfterMapping
    default void calculateTotalDays(@MappingTarget EmployeeLeave employeeLeave, ApplyLeaveVM applyLeaveVM) {
        if (applyLeaveVM.getStartDate() != null && applyLeaveVM.getEndDate() != null) {
            if (applyLeaveVM.getIsHalfDay() != null && applyLeaveVM.getIsHalfDay()) {
                employeeLeave.setTotalDays(BigDecimal.valueOf(0.5));
            } else {
                employeeLeave.setTotalDays(BigDecimal.valueOf(ChronoUnit.DAYS.between(applyLeaveVM.getStartDate(), applyLeaveVM.getEndDate()) + 1));
            }
        }
    }

}
