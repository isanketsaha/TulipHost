package com.tulip.host.mapper;

import com.tulip.host.data.DependentDTO;
import com.tulip.host.domain.Dependent;
import com.tulip.host.web.rest.vm.DependentVM;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DependentMapper {
    DependentDTO toEntity(Dependent dependent);

    @Mapping(target = "aadhaarNo", source = "aadhaar")
    @Mapping(target = "relationship", source = "relation")
    @Mapping(target = "name", expression = "java(org.apache.commons.lang.WordUtils.capitalizeFully(dependent.getName()))")
    Dependent toModel(DependentVM dependent);

    Set<Dependent> toModel(List<DependentVM> dependent);
}
