package com.tulip.host.domain;

import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "fees_order")
public class FeesOrder extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "discount")
    private Integer discount;

    @NotNull
    @Column(name = "after_discount", nullable = false)
    private Double afterDiscount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fees_catalog_id", nullable = false)
    private FeesCatalog feesCatalog;
}
