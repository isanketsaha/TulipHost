package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.PayTypeEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayVM {

    Long studentId;
    String paymentMode;
    String payType;
    List<PurchasePayVM> purchaseItems;
    List<FeePayVM> feeItem;
    double total;
}
