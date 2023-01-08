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
@Table(name = "fees_line_item")
public class FeesLineItem extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fees_order_id", nullable = false)
    private FeesOrder feesOrder;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fees_product_id", nullable = false)
    private FeesCatalog feesProduct;

    @NotNull
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Size(max = 10)
    @Column(name = "from_month", length = 10)
    private String fromMonth;

    @Size(max = 10)
    @Column(name = "to_month", length = 10)
    private String toMonth;

    @NotNull
    @Column(name = "qty", nullable = false)
    private Integer qty;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;
}
