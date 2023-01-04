package com.tulip.host.mapper;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.domain.ClassDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    ClassMapper INSTANCE = Mappers.getMapper(ClassMapper.class);

    @Mapping(target = "headTeacher", source = "headTeacher.name")
    @Mapping(target = "session", source = "session.displayText")
    ClassDetailDTO getEntityFromModel(ClassDetail student);
}
