package com.tulip.host.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;

/**
 * A DTO for the {@link com.tulip.host.domain.Fee} entity
 */
@Data
public class FeeDto implements Serializable {

    private final String createdBy;
    private final Instant createdDate;
    private final String lastModifiedBy;
    private final Instant lastModifiedDate;
    private final Long id;

    @NotNull
    private final Double amount;

    @Size(max = 10)
    private final String fromMonth;

    @Size(max = 10)
    private final String toMonth;

    private final PaymentModeDTO paymentMode;
}
