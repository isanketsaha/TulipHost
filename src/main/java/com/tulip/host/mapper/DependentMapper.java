package com.tulip.host.mapper;

import com.tulip.host.data.DependentDTO;
import com.tulip.host.domain.Dependent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DependentMapper {
    DependentDTO getEntityFromModel(Dependent dependent);
}
