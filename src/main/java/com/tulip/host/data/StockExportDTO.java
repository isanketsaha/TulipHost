package com.tulip.host.data;

import lombok.Data;

@Data
public class StockExportDTO {

    Long productID;
    String productName;
    Double price;
    String size;
    String tag;
    String std;
    int availableQty;
    int purchasedQty;
    String vendor;
}
