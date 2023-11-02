package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeePayVM {

    @NotNull
    Long feesId;

    @NotNull
    double unitPrice;

    @NotNull
    double amount;

    String month;
}
