package com.tulip.host.mapper;

import com.tulip.host.enums.BloodGroupEnum;
import org.mapstruct.Mapping;

public interface EnumMapper {
    @Mapping(target = ".", source = "")
    String map(BloodGroupEnum group);
}
