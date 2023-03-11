package com.tulip.host.data;

import com.tulip.host.enums.PayTypeEnum;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class PrintTransactionDTO {

    String transactionId;
    String studentName;
    Long studentId;
    String std;
    String paymentMode;
    PayTypeEnum payType;
    double total;
    List<PurchaseItemSummaryDTO> purchaseItems;
    List<FeesItemSummaryDTO> feesItem;
    String formattedPaymentDateTime;
}
