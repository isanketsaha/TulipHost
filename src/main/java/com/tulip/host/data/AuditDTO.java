package com.tulip.host.data;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditDTO {

    String status;
    String type;
    String endpoint;
    Instant dateTime;
}
