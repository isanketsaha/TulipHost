package com.tulip.host.mapper;

import com.tulip.host.data.LeaveTypeDto;
import com.tulip.host.domain.LeaveType;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeaveTypeMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isPaid", source = "isPaid")
    @Mapping(target = "count", source = "count")
    @Mapping(target = "isActive", source = "isActive")
    LeaveTypeDto toDto(LeaveType leaveType);

    @Mapping(target = "id", ignore = true)
    LeaveType toEntity(LeaveTypeDto leaveTypeDto);
}