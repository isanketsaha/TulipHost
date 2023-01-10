package com.tulip.host.mapper;

import com.tulip.host.data.CatalogDTO;
import com.tulip.host.domain.ProductCatalog;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductCatalogMapper {
    @Mapping(target = "std", source = "std.std")
    CatalogDTO getEntityFromModel(ProductCatalog source);

    List<CatalogDTO> getBasicEntityListFromModelList(List<ProductCatalog> student);
}
