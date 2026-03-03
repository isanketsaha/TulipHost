package com.tulip.host.web.rest.vm.dataload;

import io.github.rushuat.ocell.annotation.FieldFormat;
import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

@Data
public class InventoryRefillLoadVM {

    @FieldName("Product ID")
    private Long productCatalogId;

    @FieldName("Cost Price")
    @FieldFormat("#0.00")
    private Double unitPrice;

    @FieldName("Selling Price")
    @FieldFormat("#0.00")
    private Double mrp;

    @FieldName("Quantity")
    private Integer purchasedQty;

    @FieldName("Vendor")
    private String vendor;

    @FieldName("Discount Percent")
    private String discountPercent;
}
