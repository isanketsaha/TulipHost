package com.tulip.host.mapper;

import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.repository.impl.ReferenceMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ReferenceMapper.class })
public interface FeesCatalogMapper {
    @Mapping(target = "name", source = "feesName")
    @Mapping(target = "amount", source = "price")
    FeesCatalogDTO getEntityFromModel(FeesCatalog source);

    FeesCatalog toEntity(Long id);

    List<FeesCatalogDTO> getBasicEntityListFromModelList(List<FeesCatalog> student);
}
