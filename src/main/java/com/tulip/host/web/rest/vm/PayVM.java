package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.PayTypeEnum;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayVM {

    @NotNull
    Long studentId;

    @NotNull
    String paymentMode;

    @NotNull
    PayTypeEnum payType;

    Double subTotal;

    List<PurchasePayVM> purchaseItems;
    List<FeePayVM> feeItem;

    @NotNull
    double total;

    @NotNull
    boolean dueOpted;

    double discountAmount;

    String couponCode;

    DueVM dueInfo;
}
