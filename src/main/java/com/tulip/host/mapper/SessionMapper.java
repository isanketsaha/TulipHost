package com.tulip.host.mapper;

import com.tulip.host.data.SessionDTO;
import com.tulip.host.domain.Session;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.SessionLoadVM;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SessionMapper {
    Session toModel(SessionLoadVM loadVM);

    SessionDTO toDto(Session session);

    SessionLoadVM toVm(Session session);
}
