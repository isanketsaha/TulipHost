package com.tulip.host.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;

/**
 * A DTO for the {@link com.tulip.host.domain.PaymentMode} entity
 */
@Data
public class PaymentModeDTO implements Serializable {

    private final String createdBy;
    private final Instant createdDate;
    private final String lastModifiedBy;
    private final Instant lastModifiedDate;
    private final Long id;

    @Size(max = 20)
    @NotNull
    private final String name;
}
