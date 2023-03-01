package com.tulip.host.mapper;

import com.tulip.host.data.FeesItemSummaryDTO;
import com.tulip.host.data.PurchaseItemSummaryDTO;
import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.PurchaseLineItem;
import com.tulip.host.web.rest.vm.PurchasePayVM;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ProductCatalogMapper.class })
public interface PurchaseLineItemMapper {
    @Mapping(target = "product", source = "productId")
    PurchaseLineItem toModel(PurchasePayVM lineItem);

    Set<PurchaseLineItem> toModelList(List<PurchasePayVM> classDetails);

    @Mapping(target = "productTitle", source = "product.itemName")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "productCategory", source = "product.category")
    @Mapping(target = "productTag", source = "product.tag")
    @Mapping(target = "productSize", source = "product.size")
    PurchaseItemSummaryDTO toEntity(PurchaseLineItem feesOrder);

    List<PurchaseItemSummaryDTO> toEntityList(Set<PurchaseLineItem> classDetails);
}
