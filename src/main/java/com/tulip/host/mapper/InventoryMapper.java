package com.tulip.host.mapper;

import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.domain.Inventory;
import com.tulip.host.web.rest.vm.ProductLoadVM;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ProductCatalogMapper.class })
public interface InventoryMapper {
    @Mapping(target = "purchasedQty", source = "qty")
    @Mapping(
        target = "availableQty",
        expression = "java(source.getQty() - source.getProduct()\n" +
        "            .getPurchaseLineItems()\n" +
        "            .stream()\n" +
        "            .mapToInt(lineItem -> lineItem.getQty())\n" +
        "            .sum())"
    )
    @Mapping(
        target = "lowStock",
        expression = "java(source.getQty() - source.getProduct()\n" +
        "            .getPurchaseLineItems()\n" +
        "            .stream()\n" +
        "            .mapToInt(lineItem -> lineItem.getQty())\n" +
        "            .sum() < 10 ? true : false)"
    )
    InventoryItemDTO toEntity(Inventory source);

    List<InventoryItemDTO> toEntityList(List<Inventory> source);

    @Mapping(target = "unitPrice", source = "purchasePrice")
    Inventory toModel(ProductLoadVM source);
}
