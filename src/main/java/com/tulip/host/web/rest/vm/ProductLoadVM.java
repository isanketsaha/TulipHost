package com.tulip.host.web.rest.vm;

import io.github.rushuat.ocell.annotation.FieldFormat;
import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

@Data
public class ProductLoadVM {

    @FieldName("Product Name")
    private String itemName;

    @FieldName("Price")
    @FieldFormat("#0.00")
    private Double price;

    @FieldName("Tag")
    private String tag;

    @FieldName("Class")
    private String classDetail;

    @FieldName("Session")
    private Long session;

    @FieldName("Size")
    private String size;

    @FieldName("Category")
    private String category;

    @FieldName("Cost Price")
    private int purchasePrice;

    @FieldName("Quantity")
    private int qty;

    @FieldName("Discount Percent")
    private String discountPercent;

    @FieldName("Vendor")
    private String vendor;
}
