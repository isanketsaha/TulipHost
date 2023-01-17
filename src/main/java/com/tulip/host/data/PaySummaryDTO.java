package com.tulip.host.data;

import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.web.rest.vm.PurchasePayVM;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaySummaryDTO {

    String studentId;
    String studentName;
    String paymentMode;
    PayTypeEnum payType;
    List<PurchaseItemSummaryDTO> purchaseItems;
    List<FeesItemSummaryDTO> feesItem;
    double total;
    Date paymentDateTime;
    String paymentReceivedBy;
}