package com.tulip.host.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeesGraphDTO {

    LocalDateTime admissionDate;
    Set<String> paidMonths;
    Set<Long> annualFeesPaid;
    Set<String> transportMonths;
}
