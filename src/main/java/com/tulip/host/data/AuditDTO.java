package com.tulip.host.data;

import java.time.Instant;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditDTO {

    Long id;
    String status;
    String type;
    String description;
    String endpoint;
    Instant dateTime;
}
