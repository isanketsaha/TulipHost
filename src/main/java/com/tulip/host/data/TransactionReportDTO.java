package com.tulip.host.data;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReportDTO {

    Date transactionDate;
    double expense;
    double fees;
    double purchase;
    double total;
}
