package com.tulip.host.domain;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "purchase_order")
public class PurchaseOrder extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;
}
