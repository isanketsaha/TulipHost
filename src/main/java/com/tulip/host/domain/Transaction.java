package com.tulip.host.domain;

import java.time.Instant;
import java.util.Set;
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
@Table(name = "transactions")
public class Transaction extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "payment_mode", nullable = false, length = 20)
    private String paymentMode;

    @Size(max = 20)
    @NotNull
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "discount")
    private Integer discount;

    @NotNull
    @Column(name = "after_discount", nullable = false)
    private Double afterDiscount;

    @Size(max = 100)
    @Column(name = "comments", length = 100)
    private String comments;

    @OneToMany(mappedBy = "order")
    private Set<FeesLineItem> feesLineItem;

    @OneToMany(mappedBy = "order")
    private Set<PurchaseLineItem> purchaseLineItems;

    @OneToMany(mappedBy = "order")
    private Set<Expense> expenseItems;
}
