package com.tulip.host.data;

import lombok.Data;

@Data
public class InventoryItemDTO {

    Long id;
    ProductDTO product;
    int purchasedQty;
    int availableQty;
    Boolean lowStock;
    String vendor;
}
