package com.tulip.host.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.tulip.host.data.ProductDTO;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.repository.ProductCatalogRepository;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.web.rest.vm.dataload.ProductLoadVM;

@Mapper(componentModel = "spring", uses = { ReferenceMapper.class })
public abstract class ProductCatalogMapper {

    @Autowired
    public ProductCatalogRepository productCatalogRepository;

    @Mapping(target = "std", source = "std.std")
    @Mapping(target = "type", source = "category")
    @Mapping(target = "category", source = "category")
    public abstract ProductDTO toEntity(ProductCatalog source);


    @Mapping(expression = "java(productLoadVM.getItemName().toUpperCase())", target = "itemName")
    public abstract ProductCatalog toModel(ProductLoadVM productLoadVM);

    public abstract List<ProductDTO> toModelList(List<ProductCatalog> student);

    protected ProductCatalog toModel(Long value) {
        if (value == null) {
            return null;
        }
        return productCatalogRepository.findById(value)
                .orElse(null);
    }

    @AfterMapping
    protected void setPrice(ProductCatalog source, @MappingTarget ProductDTO target) {
        double highestPrice = source.getInventories()
                .stream()
                .filter(inv -> inv.getActive()) // Only consider active inventory batches
                .mapToDouble(inventory -> inventory.getUnitPrice())
                .max()
                .orElse(0.0);
        target.setPrice(highestPrice > 0 ? highestPrice : null);
    }
}
