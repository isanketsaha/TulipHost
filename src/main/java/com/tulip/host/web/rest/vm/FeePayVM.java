package com.tulip.host.web.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeePayVM {

    Long feesId;
    double unitPrice;
    double amount;
    String from;
    String to;
}
