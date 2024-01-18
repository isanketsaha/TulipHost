package com.tulip.host.web.rest.vm;

import java.util.List;
import lombok.Data;

@Data
public class ExpenseVm {

    List<ExpenseItemVM> expenseItem;
    String receivedBy;
    String comments;
    List<FileUploadVM> expenseDocs;
    double total;
    String paymentMode;
}
