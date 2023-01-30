package com.tulip.host.mapper;

import com.tulip.host.data.CatalogDTO;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.web.rest.vm.ProductLoadVM;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ReferenceMapper.class })
public interface ProductCatalogMapper {
    @Mapping(target = "std", source = "std.std")
    @Mapping(target = "type", source = "category")
    CatalogDTO toEntity(ProductCatalog source);

    ProductCatalog toModel(Long id);

    ProductCatalog toModel(ProductLoadVM productLoadVM);

    List<CatalogDTO> toModelList(List<ProductCatalog> student);
}
