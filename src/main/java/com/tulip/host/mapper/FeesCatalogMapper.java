package com.tulip.host.mapper;

import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.web.rest.vm.FeesLoadVM;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ReferenceMapper.class })
public interface FeesCatalogMapper {
    @Mapping(target = "name", source = "feesName")
    @Mapping(target = "amount", source = "price")
    FeesCatalogDTO toEntity(FeesCatalog source);

    FeesCatalog toModel(Long id);

    ClassDetail toClassModel(Long id);

    List<FeesCatalogDTO> toEntityList(List<FeesCatalog> catalogs);

    List<FeesCatalogDTO> toEntityList(Set<FeesCatalog> catalogs);

    FeesCatalog toModel(FeesLoadVM feesLoadVM);

    List<FeesCatalog> toModelList(List<FeesLoadVM> feesLoadVM);
}
