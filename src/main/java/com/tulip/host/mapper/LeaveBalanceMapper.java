package com.tulip.host.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.tulip.host.data.LeaveBalanceDTO;
import com.tulip.host.domain.LeaveType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeaveBalanceMapper {

//    @Mapping(target = "totalAllowed", source = "count")
//    @Mapping(target = "usedCount", source = "usedDays")
//    @Mapping(target = "availableCount", expression = "java((double)(leaveType.getCount() - (usedDays != null ? usedDays.intValue() : 0)))")
//    LeaveBalanceDTO toDto(LeaveType leaveType);

    default LeaveBalanceDTO createLeaveBalance(LeaveType leaveType, BigDecimal usedDays) {
        if (leaveType == null) {
            return null;
        }
        LeaveBalanceDTO dto = new LeaveBalanceDTO();
        dto.setId(leaveType.getId());
        dto.setName(leaveType.getName());
        dto.setDescription(leaveType.getDescription());
        dto.setIsPaid(leaveType.getIsPaid());
        dto.setIsActive(leaveType.getIsActive());
        dto.setTotalAllowed(leaveType.getCount());
        dto.setUsedCount(usedDays != null ? usedDays.doubleValue() : 0.0);
        dto.setAvailableCount(leaveType.getCount() - (usedDays != null ? usedDays.doubleValue() : 0.0));
        return dto;
    }
}
