package com.tulip.host.data.pojo;

import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * A DTO for the {@link com.tulip.host.domain.PurchaseOrder} entity
 */
@Data
public class PurchaseOrderPojo implements Serializable {

    private final String createdBy;
    private final Instant createdDate;
    private final String lastModifiedBy;
    private final Instant lastModifiedDate;
    private final Long id;

    @NotNull
    private final Double amount;

    @NotNull
    private final Integer purchase;
}
