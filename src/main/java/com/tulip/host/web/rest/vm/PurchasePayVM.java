package com.tulip.host.web.rest.vm;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasePayVM {

    @NotNull
    Long productId;

    String size;

    @NotNull
    int qty;

    @NotNull
    double unitPrice;

    @NotNull
    double amount;
}
