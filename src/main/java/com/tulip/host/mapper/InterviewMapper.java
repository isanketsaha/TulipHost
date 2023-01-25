package com.tulip.host.mapper;

import com.tulip.host.data.InterviewDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.Interview;
import com.tulip.host.web.rest.vm.InterviewVM;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InterviewMapper {
    InterviewMapper INSTANCE = Mappers.getMapper(InterviewMapper.class);

    InterviewDTO toEntity(Interview source);

    Interview fromEntity(InterviewVM source);

    default Employee map(List<InterviewVM> source) {
        return map(source.get(0));
    }

    @Mapping(target = "interview", source = ".")
    Employee map(InterviewVM source);
}
