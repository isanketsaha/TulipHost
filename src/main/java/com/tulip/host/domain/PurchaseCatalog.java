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
@Table(name = "purchase_catalog")
public class PurchaseCatalog extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @NotNull
    @Column(name = "cost_price", nullable = false)
    private Double costPrice;

    @NotNull
    @Column(name = "sell_price", nullable = false)
    private Double sellPrice;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 10)
    @NotNull
    @Column(name = "tag", nullable = false, length = 10)
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id")
    private ClassDetail std;

    @Size(max = 20)
    @Column(name = "size", length = 20)
    private String size;
}
