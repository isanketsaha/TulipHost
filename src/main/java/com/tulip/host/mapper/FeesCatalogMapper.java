package com.tulip.host.mapper;

import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.repository.impl.ReferenceMapper;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ReferenceMapper.class })
public interface FeesCatalogMapper {
    @Mapping(target = "name", source = "feesName")
    @Mapping(target = "amount", source = "price")
    FeesCatalogDTO toEntity(FeesCatalog source);

    FeesCatalog toEntity(Long id);

    List<FeesCatalogDTO> toEntityList(List<FeesCatalog> catalogs);

    List<FeesCatalogDTO> toEntityList(Set<FeesCatalog> catalogs);
}
