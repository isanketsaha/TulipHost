package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryUpdateVM {

    @NotNull
    Long productCatalogId;

    @NotNull
    Double unitPrice;

    @NotNull
    Double mrp;

    @NotNull
    Integer purchasedQty;

    String discountPercent;

    String vendor;

    // Flag to deactivate all active inventory batches for this product when creating a new refill
    boolean deactivateOld = false;
}
