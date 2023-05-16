package com.tulip.host.domain;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "transactions")
@FilterDef(name = "filterTransactionOnType", defaultCondition = "type = :type ", parameters = @ParamDef(name = "type", type = "string"))
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

    @OneToMany(
        mappedBy = "order",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH },
        orphanRemoval = true
    )
    private Set<Expense> expenseItems;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = true)
    private Set<Upload> uploadList;

    public void removeFeesLineItem(FeesLineItem lineItem) {
        this.feesLineItem.remove(lineItem);
    }

    public void removePurchaseLineItems(PurchaseLineItem lineItem) {
        this.purchaseLineItems.remove(lineItem);
    }

    public void removeExpenseLineItems(Expense lineItem) {
        this.expenseItems.remove(lineItem);
    }
}
