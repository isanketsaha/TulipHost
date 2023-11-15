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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "expense")
public class Expense extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "itemName", length = 50)
    private String itemName;

    @Size(max = 500)
    @NotNull
    @Column(name = "receivedBy", nullable = false, length = 500)
    private String receivedBy;

    @Size(max = 100)
    @Column(name = "category", length = 100)
    private String category;

    @NotNull
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @Size(max = 10)
    @NotNull
    @Column(name = "qty", nullable = false, length = 10)
    private String qty;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction order;
}
