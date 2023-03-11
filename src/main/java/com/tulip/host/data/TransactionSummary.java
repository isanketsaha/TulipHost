package com.tulip.host.data;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionSummary {

    double feesTotal;
    double expenseTotal;
    double purchaseTotal;
    double amountTotal;
    List<TransactionReportDTO> reportList;
}
