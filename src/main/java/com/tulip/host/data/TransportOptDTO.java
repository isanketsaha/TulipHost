package com.tulip.host.data;

import java.time.Instant;
import lombok.Data;

@Data
public class TransportOptDTO extends TransportCatalogDto {

    private Instant startDate;
    private Instant endDate;
}
