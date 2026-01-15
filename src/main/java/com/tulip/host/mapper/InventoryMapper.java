package com.tulip.host.mapper;

import java.util.List;
import java.util.Map;

import com.tulip.host.data.ProductDTO;
import com.tulip.host.domain.ProductCatalog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.data.StockExportDTO;
import com.tulip.host.domain.Inventory;
import com.tulip.host.web.rest.vm.dataload.ProductLoadVM;

@Mapper(componentModel = "spring", uses = {ProductCatalogMapper.class})
public interface InventoryMapper {
//    @Mapping(target = "purchasedQty", source = "qty")
//    @Mapping(
//        target = "availableQty",
//        expression = "java(source.getQty() - source.getPurchaseLineItems()\n" +
//            "            .stream()\n" +
//            "            .mapToInt(lineItem -> lineItem.getQty())\n" +
//            "            .sum())"
//    )
//    @Mapping(
//        target = "lowStock",
//        expression = "java(source.getQty() - source.getPurchaseLineItems()\n" +
//            "            .stream()\n" +
//            "            .mapToInt(lineItem -> lineItem.getQty())\n" +
//            "            .sum() < 10 ? true : false)"
//    )
//    InventoryItemDTO toEntity(Inventory source);
//
//    List<InventoryItemDTO> toEntityList(List<Inventory> source);


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
//
//    @Mapping(target = "purchasedQty", source = "qty")
//    @Mapping(
//        target = "availableQty",
//        expression = "java(source.getQty() - source.getPurchaseLineItems()\n" +
//            "            .stream()\n" +
//            "            .mapToInt(lineItem -> lineItem.getQty())\n" +
//            "            .sum())"
//    )
//    @Mapping(target = "productID", source = "product.id")
//    @Mapping(target = "productName", source = "product.itemName")
//    @Mapping(target = "price", expression = "java(calculateHighestInventoryPrice(source.getProduct()))")
//    @Mapping(target = "tag", source = "product.tag")
//    @Mapping(target = "std", source = "product.std.std")
//    @Mapping(target = "size", source = "product.size")
//    StockExportDTO toExportEntity(Inventory source);
//
//    List<StockExportDTO> toExportEntityList(List<Inventory> source);

    /**
     * Create aggregated InventoryItemDTO from product catalog and inventory list
     * Aggregates quantities across multiple inventory batches for the same product
     */
    default InventoryItemDTO toAggregatedInventoryItemDTO(ProductCatalog productCatalog, List<Inventory> inventories ) {
        if (inventories == null || inventories.isEmpty()) {
            return null;
        }

        int totalPurchasedQty = inventories.stream()
                .mapToInt(Inventory::getQty)
                .sum();

        int totalUsedQty = inventories.stream()
                .flatMap(inv -> inv.getPurchaseLineItems().stream())
                .mapToInt(com.tulip.host.domain.PurchaseLineItem::getQty)
                .sum();

        int totalAvailableQty = totalPurchasedQty - totalUsedQty;
        Inventory latestInventory = inventories.get(0);
        InventoryItemDTO dto = new InventoryItemDTO();
        dto.setId(latestInventory.getId());
        dto.setProduct(toProductDTO(productCatalog));
        dto.setPurchasedQty(totalPurchasedQty);
        dto.setAvailableQty(totalAvailableQty);
        dto.setLowStock(totalAvailableQty < 10);
        dto.setVendor(latestInventory.getVendor());
        return dto;
    }

    /**
     * Create aggregated StockExportDTO from product catalog and inventory list
     * Aggregates quantities across multiple inventory batches for the same product
     */
    default StockExportDTO toAggregatedStockExportDTO(ProductCatalog productCatalog, List<Inventory> inventories) {
        if (inventories == null || inventories.isEmpty()) {
            return null;
        }

        int totalPurchasedQty = inventories.stream()
                .mapToInt(Inventory::getQty)
                .sum();

        int totalUsedQty = inventories.stream()
                .flatMap(inv -> inv.getPurchaseLineItems().stream())
                .mapToInt(com.tulip.host.domain.PurchaseLineItem::getQty)
                .sum();

        int totalAvailableQty = totalPurchasedQty - totalUsedQty;
        Inventory latestInventory = inventories.get(0);

        StockExportDTO dto = new StockExportDTO();
        dto.setProductID(productCatalog.getId());
        dto.setProductName(productCatalog.getItemName());
        dto.setPrice(calculateHighestInventoryPrice(inventories));
        dto.setTag(productCatalog.getTag());
        dto.setStd(productCatalog.getStd() != null ? productCatalog.getStd().getStd() : null);
        dto.setSize(productCatalog.getSize());
        dto.setPurchasedQty(totalPurchasedQty);
        dto.setAvailableQty(totalAvailableQty);
        dto.setVendor(latestInventory.getVendor());

        return dto;
    }

    /**
     * Convert ProductCatalog to ProductDTO
     */
    @Mapping(
        target = "std",
        source = "std.std"
    )
    ProductDTO toProductDTO(ProductCatalog source);

    /**
     * Calculate the highest inventory price for a product.
     * Only considers active inventory batches.
     * ProductCatalog price field is never used - pricing comes purely from Inventory.
     */
    default Double calculateHighestInventoryPrice(List<Inventory> inventories) {
        double maxPrice = inventories
            .stream()
            .filter(Inventory::getActive)
            .mapToDouble(Inventory::getUnitPrice)
            .max()
            .orElse(0.0);
        return maxPrice > 0 ? maxPrice : null;
    }

}
