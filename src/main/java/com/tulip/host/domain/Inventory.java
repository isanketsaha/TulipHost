package com.tulip.host.domain;

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
@Table(name = "inventory")
public class Inventory extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductCatalog product;

    @NotNull
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @NotNull
    @Column(name = "qty", nullable = false)
    private Integer qty;

    @NotNull
    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "discountPercent")
    private Integer discountPercent;

    @Size(max = 50)
    @Column(name = "vendor", length = 50)
    private String vendor;
}
