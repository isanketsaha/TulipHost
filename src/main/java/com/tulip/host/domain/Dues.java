package com.tulip.host.domain;

import com.tulip.host.enums.DueStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "Dues")
public class Dues extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @NotNull
    @Column(name = "dueDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @NotNull
    @Column(name = "amount", nullable = false)
    private double dueAmount;

    @NotNull
    @Column(name = "approvedBy", nullable = false)
    private String approvedBy;

    @OneToMany(
        mappedBy = "due",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH },
        orphanRemoval = true
    )
    private List<DuesPayment> duesPayment;

    @Builder.Default
    @NotNull
    @Column(name = "status", nullable = false)
    private String status = DueStatusEnum.ACTIVE.name();

    @Builder.Default
    @Column(name = "penalty", nullable = false)
    private double penalty = 0;

    public void addDuesPayment(DuesPayment payment) {
        if (duesPayment == null) {
            List<DuesPayment> classList = new ArrayList<>();
            classList.add(payment);
            this.setDuesPayment(classList);
        } else {
            duesPayment.add(payment);
        }
    }
}
