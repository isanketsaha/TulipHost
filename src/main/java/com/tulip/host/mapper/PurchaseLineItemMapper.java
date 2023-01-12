package com.tulip.host.mapper;

import com.tulip.host.domain.PurchaseLineItem;
import com.tulip.host.web.rest.vm.PurchasePayVM;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ProductCatalogMapper.class })
public interface PurchaseLineItemMapper {
    @Mapping(target = "product", source = "productTitle")
    PurchaseLineItem toEntity(PurchasePayVM lineItem);

    Set<PurchaseLineItem> toEntityList(List<PurchasePayVM> classDetails);
}
