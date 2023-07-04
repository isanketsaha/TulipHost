package com.tulip.host.data;

import com.tulip.host.enums.ExpenseTypeEnum;
import lombok.Data;

@Data
public class ExpenseItemDTO {

    Long id;
    String itemName;
    ExpenseTypeEnum category;
    double amount;
    int qty;
    double unitPrice;
    String receivedBy;
}
