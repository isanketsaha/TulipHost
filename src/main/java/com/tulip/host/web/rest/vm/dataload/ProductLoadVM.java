package com.tulip.host.web.rest.vm.dataload;

import io.github.rushuat.ocell.annotation.FieldFormat;
import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

@Data
public class ProductLoadVM extends DataLoadVM {

    @FieldName("Tag")
    private String tag;

    @FieldName("Session")
    private Long session;

    @FieldName("Size")
    private String size;

    @FieldName("Category")
    private String category;

    @FieldName("Cost Price")
    private Double purchasePrice;

    @FieldName("Quantity")
    private Integer qty;

    @FieldName("Discount Percent")
    private Integer discountPercent;

    @FieldName("Vendor")
    private String vendor;
}
