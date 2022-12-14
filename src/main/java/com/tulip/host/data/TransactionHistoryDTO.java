package com.tulip.host.data;

import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * A DTO for the {@link com.tulip.host.domain.TransactionHistory} entity
 */
@Data
public class TransactionHistoryDTO implements Serializable {

    private final String createdBy;
    private final Instant createdDate;
    private final String lastModifiedBy;
    private final Instant lastModifiedDate;
    private final Long id;

    @NotNull
    private final Long paymentModeId;

    private final PurchaseOrderDTO purchaseOrder;
    private final FeeDto fees;

    @NotNull
    private final Double totalAmount;

    @NotNull
    private final StudentDetailsDTO student;

    @Size(max = 100)
    private final String comments;

    private final Integer discount;

    @NotNull
    private final Double afterDiscount;
}
