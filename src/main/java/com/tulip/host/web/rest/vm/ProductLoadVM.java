package com.tulip.host.web.rest.vm;

import com.tulip.host.mapper.DoubleConverter;
import io.github.rushuat.ocell.annotation.FieldConverter;
import io.github.rushuat.ocell.annotation.FieldFormat;
import io.github.rushuat.ocell.annotation.FieldName;
import io.github.rushuat.ocell.annotation.StringValue;
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
    private Double purchasePrice;

    @FieldName("Quantity")
    private int qty;

    @FieldName("Discount Percent")
    private Integer discountPercent;

    @FieldName("Vendor")
    private String vendor;
}
