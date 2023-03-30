package com.tulip.host.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
