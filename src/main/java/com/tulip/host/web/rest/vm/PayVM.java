package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.PayTypeEnum;
import java.util.List;
import javax.validation.constraints.NotNull;
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

    List<PurchasePayVM> purchaseItems;
    List<FeePayVM> feeItem;

    @NotNull
    double total;

    @NotNull
    boolean dueOpted;

    DueVM dueInfo;
}
