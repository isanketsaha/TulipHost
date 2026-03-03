package com.tulip.host.data;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class InventoryBatchDTO {

    Long id;
    int purchasedQty;
    int availableQty;
    Double purchasePrice;
    Double mrp;
    String vendor;
    String discountPercent;
    Boolean active;
    LocalDateTime createdDate;
    String createdBy;
}
