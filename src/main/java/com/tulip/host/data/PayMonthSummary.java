package com.tulip.host.data;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayMonthSummary {

    Instant createdDate;
    String fromMonth;
    String toMonth;
    String feesName;
}
