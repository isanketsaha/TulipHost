package com.tulip.host.mapper;

import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.domain.FeesCatalog;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeesCatalogMapper {
    @Mapping(target = "name", source = "feesName")
    @Mapping(target = "amount", source = "price")
    FeesCatalogDTO getEntityFromModel(FeesCatalog source);

    List<FeesCatalogDTO> getBasicEntityListFromModelList(List<FeesCatalog> student);
}
