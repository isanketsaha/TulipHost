package com.tulip.host.data;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class PrintTransactionDTO {

    Long transactionId;
    String currentDate;
    String studentName;
    String std;
    String paymentMode;
    List<PurchaseItemSummaryDTO> purchaseItems;
    List<FeesItemSummaryDTO> feesItem;
    double total;
    Date paymentDateTime;
}
