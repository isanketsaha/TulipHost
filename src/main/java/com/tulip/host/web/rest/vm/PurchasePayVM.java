package com.tulip.host.web.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasePayVM {

    Long productTitle;
    String size;
    int qty;
    double unitPrice;
    double amount;
}
