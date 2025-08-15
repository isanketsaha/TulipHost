package com.tulip.host.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItemSummaryDTO {

    Long productId;
    Long itemId;
    String productTitle;
    String productCategory;
    String productTag;
    String productSize;
    int qty;
    double unitPrice;
    double amount;
    Long inventoryId;
    String inventoryVendor;
}
