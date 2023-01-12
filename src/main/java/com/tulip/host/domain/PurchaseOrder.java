package com.tulip.host.domain;

import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "discount")
    private Integer discount;

    @NotNull
    @Column(name = "after_discount", nullable = false)
    private Double afterDiscount;

    @Lob
    @Column(name = "note")
    private String note;

    @NotNull
    @Column(name = "payment_mode", nullable = false)
    private String paymentMode;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "purchaseOrder", cascade = CascadeType.PERSIST)
    private Set<PurchaseLineItem> lineItem;
}
