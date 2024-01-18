package com.tulip.host.web.rest.vm.dataload;

import io.github.rushuat.ocell.annotation.FieldFormat;
import io.github.rushuat.ocell.annotation.FieldName;
import lombok.Data;

@Data
public class DataLoadVM {

    @FieldName("Product Name")
    private String itemName;

    @FieldName("Price")
    @FieldFormat("#0.00")
    private Double price;

    @FieldName("Class")
    private String classDetail;
}
