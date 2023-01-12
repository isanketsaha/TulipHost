package com.tulip.host.mapper;

import com.tulip.host.data.CatalogDTO;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.repository.impl.ReferenceMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ReferenceMapper.class })
public interface ProductCatalogMapper {
    @Mapping(target = "std", source = "std.std")
    CatalogDTO toModel(ProductCatalog source);

    ProductCatalog toEntity(Long id);

    List<CatalogDTO> toModelList(List<ProductCatalog> student);
}
