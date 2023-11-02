package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DuePaymentVm {

    @NotNull
    double amount;

    @NotNull
    long dueId;

    @NotNull
    String paymentMode;

    double penalty;

    @NotNull
    double totalAmount;

    @NotNull
    long transactionId;
}
