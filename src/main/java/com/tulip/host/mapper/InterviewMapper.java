package com.tulip.host.mapper;

import com.tulip.host.data.InterviewDTO;
import com.tulip.host.domain.Interview;
import com.tulip.host.web.rest.vm.InterviewVM;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InterviewMapper {
    InterviewMapper INSTANCE = Mappers.getMapper(InterviewMapper.class);

    InterviewDTO getEntityFromModel(Interview source);

    Interview getModelFromEntity(InterviewVM source);
}
