package com.tulip.host.data;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeesGraphDTO {

    Instant admissionDate;
    Set<String> paidMonths;
}
