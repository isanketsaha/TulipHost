package com.tulip.host.mapper;

import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.data.StockExportDTO;
import com.tulip.host.domain.Inventory;
import com.tulip.host.web.rest.vm.dataload.ProductLoadVM;
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

    @Mapping(
        target = "discountPercent",
        expression = "java(source.getDiscountPercent() == null ? com.tulip.host.utils" +
        ".CommonUtils.calculateDiscountPercent(source.getPurchasePrice(),source.getPrice()) : String.valueOf(source.getDiscountPercent()))"
    )
    @Mapping(
        target = "unitPrice",
        expression = "java(source.getPurchasePrice() == null ? com.tulip.host.utils" +
        ".CommonUtils.calculatePurchasePrice(source.getDiscountPercent(), source.getPrice()) : source.getPurchasePrice())"
    )
    Inventory toModel(ProductLoadVM source);

    @Mapping(target = "purchasedQty", source = "qty")
    @Mapping(
        target = "availableQty",
        expression = "java(source.getQty() - source.getProduct()\n" +
        "            .getPurchaseLineItems()\n" +
        "            .stream()\n" +
        "            .mapToInt(lineItem -> lineItem.getQty())\n" +
        "            .sum())"
    )
    @Mapping(target = "productID", source = "product.id")
    @Mapping(target = "productName", source = "product.itemName")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "tag", source = "product.tag")
    @Mapping(target = "std", source = "product.std.std")
    @Mapping(target = "size", source = "product.size")
    StockExportDTO toExportEntity(Inventory source);

    List<StockExportDTO> toExportEntityList(List<Inventory> source);
}
