package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryUpdateVM {

    @NotNull
    Long inventoryId;

    @NotNull
    Double unitPrice;

    @NotNull
    Integer purchasedQty;

    String discountPercent;

    String vendor;

    // Flag to deactivate the old inventory batch when creating a new refill
    boolean deactivateOld = false;
}
