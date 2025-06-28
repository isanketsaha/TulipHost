package com.tulip.host.domain;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "coupons")
@Data
public class Coupon extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "min_purchase_amount")
    private BigDecimal minPurchaseAmount;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "usage_limit")
    private Integer usageLimit = 1;

    @OneToMany(
        mappedBy = "couponId",
        fetch = FetchType.LAZY,
        cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH},
        orphanRemoval = true
    )
    private Set<Transaction> transactionsList = new LinkedHashSet<>();

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }

    public void addToTransactionList(Transaction transaction) {
        if (transactionsList == null) {
            Set<Transaction> coupons = new LinkedHashSet<>();
            coupons.add(transaction);
            this.setTransactionsList(coupons);
        } else {
            this.transactionsList.add(transaction);
        }
    }
}
