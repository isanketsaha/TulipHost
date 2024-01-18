package com.tulip.host.data;

import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.web.rest.vm.FileUploadVM;
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
    String paymentId;
    PayTypeEnum payType;
    List<PurchaseItemSummaryDTO> purchaseItems;
    List<FeesItemSummaryDTO> feesItem;
    List<ExpenseItemDTO> expenseItems;
    double total;
    String comments;
    Date paymentDateTime;
    String createdBy;
    List<FileUploadVM> docs;
    boolean dueOpted;
    DuesDTO dues;
}
