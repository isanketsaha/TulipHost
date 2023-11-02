package com.tulip.host.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "fees_product_id", nullable = false)
    private FeesCatalog feesProduct;

    @NotNull
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Size(max = 10)
    @Column(name = "month", length = 10)
    private String month;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;
}
