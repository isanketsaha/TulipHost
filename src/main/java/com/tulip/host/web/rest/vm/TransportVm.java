package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Data;

@Data
public class TransportVm {

    @NotNull
    Long studentId;

    Long locationId;
    Instant startDate;
}
