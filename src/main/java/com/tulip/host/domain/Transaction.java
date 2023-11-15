package com.tulip.host.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
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
@Entity
@Table(name = "transactions")
@FilterDef(name = "filterTransactionOnType", defaultCondition = "type = :type ", parameters = @ParamDef(name = "type", type = String.class))
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

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "invoice_id")
    private Upload invoice;

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

    @Builder.Default
    @Column(name = "due_opted", nullable = false)
    private Boolean dueOpted = false;

    @OneToOne(mappedBy = "transaction", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Dues dues;

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

    @OneToMany(
        mappedBy = "transactionDocs",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.MERGE, CascadeType.PERSIST },
        orphanRemoval = true
    )
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

    public void addToUploadList(Upload upload) {
        if (uploadList == null) {
            Set<Upload> uploads = new LinkedHashSet<>();
            uploads.add(upload);
            this.setUploadList(uploads);
        } else {
            this.uploadList.add(upload);
        }
    }
}
