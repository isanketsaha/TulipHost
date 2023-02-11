package com.tulip.host.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeesItemSummaryDTO {

    Long feesId;
    String feesTitle;
    double unitPrice;
    double amount;
    String applicableRule;
    String month;
}
