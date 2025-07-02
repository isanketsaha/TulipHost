package com.tulip.host.mapper;

import com.tulip.host.domain.LeaveType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LeaveTypeMapper {

    @Mapping(target = "id", ignore = true)
    // @Mapping(target = "createdDate", ignore = true)
    // @Mapping(target = "createdBy", ignore = true)
    void updateEntityFromEntity(LeaveType source, @MappingTarget LeaveType target);
}