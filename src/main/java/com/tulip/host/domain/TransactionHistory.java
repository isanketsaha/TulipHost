package com.tulip.host.domain;

import java.io.Serializable;
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
@Table(name = "transaction_history")
public class TransactionHistory extends AbstractAuditingEntity<Long> implements Serializable {

    @Id
    @Column(name = "transaction_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "payment_mode_id", nullable = false)
    private Long paymentModeId;

    @Column(name = "purchase_order_id")
    private Long purchaseOrder;

    @Column(name = "fees_id")
    private Long fees;

    @NotNull
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @NotNull
    @Column(name = "student_id", nullable = false)
    private Long student;

    @Size(max = 100)
    @Column(name = "comments", length = 100)
    private String comments;

    @Column(name = "discount")
    private Integer discount;

    @NotNull
    @Column(name = "after_discount", nullable = false)
    private Double afterDiscount;
}