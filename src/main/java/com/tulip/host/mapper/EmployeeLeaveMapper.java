package com.tulip.host.mapper;

import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.web.rest.vm.ApplyLeaveVM;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface EmployeeLeaveMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true) // Will be set in AfterMapping
    @Mapping(target = "leaveType", ignore = true) // Will be set in AfterMapping
    @Mapping(target = "totalDays", ignore = true) // Will be calculated in AfterMapping
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "approvalDate", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdDate", ignore = true) // Will be set manually
    @Mapping(target = "lastModifiedDate", ignore = true) // Will be set manually
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    EmployeeLeave toEntity(ApplyLeaveVM applyLeaveVM);

    @AfterMapping
    default void map(@MappingTarget EmployeeLeave target, ApplyLeaveVM source) {
        // Calculate total days between start and end date (inclusive)
        if (source.getStartDate() != null && source.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(source.getStartDate(), source.getEndDate()) + 1;
            target.setTotalDays(BigDecimal.valueOf(days));
        }
    }
}