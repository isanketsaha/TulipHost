//package com.tulip.host.domain;
//
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Set;
//
//@Entity
//@Table(name = "coupon_usage")
//@Data
//public class CouponUsage extends AbstractAuditingEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "orderId", nullable = false)
//    private Coupon coupon;
//
//    @OneToMany(
//        mappedBy = "coupon",
//        fetch = FetchType.LAZY,
//        cascade = { CascadeType.MERGE, CascadeType.PERSIST },
//        orphanRemoval = true
//    )
//    private Set<Transaction> orderId;
//
//    @Column(name = "used_at")
//    private LocalDateTime usedAt;
//}
