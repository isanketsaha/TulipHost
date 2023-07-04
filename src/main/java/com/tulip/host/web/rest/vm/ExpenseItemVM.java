package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.ExpenseTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseItemVM {

    String itemName;
    ExpenseTypeEnum category;
    double unitPrice;
    double amount;
    int qty;
}
