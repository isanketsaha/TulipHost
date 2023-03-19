package com.tulip.host.domain;

import java.time.Instant;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.ParamDef;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "transactions")
@FilterDef(name = "filterTransactionOnType", parameters = @ParamDef(name = "type", type = "string"))
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
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

    @OneToMany(
        mappedBy = "order",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH },
        orphanRemoval = true
    )
    private Set<FeesLineItem> feesLineItem;

    @OneToMany(
        mappedBy = "order",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH },
        orphanRemoval = true
    )
    private Set<PurchaseLineItem> purchaseLineItems;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<Expense> expenseItems;

    public void removeFeesLineItem(FeesLineItem lineItem) {
        this.feesLineItem.remove(lineItem);
    }

    public void removePurchaseLineItems(PurchaseLineItem lineItem) {
        this.purchaseLineItems.remove(lineItem);
    }
}
