package com.tulip.host.web.rest.vm;

import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

@Data
public class ProductLoadVM {

    @FieldName("Product Name")
    private String itemName;

    @FieldName("Sell Price")
    private Double price;

    @FieldName("Tag")
    private String tag;

    @FieldName("Class")
    private String classDetail;

    @FieldName("Session")
    private Long session;

    @FieldName("size")
    private String size;

    @FieldName("category")
    private String category;

    @FieldName("Purchase Price")
    private Double purchasePrice;

    @FieldName("qty")
    private Integer qty;

    @FieldName("discount percent")
    private Integer discountPercent;

    @FieldName("vendor")
    private String vendor;
}
