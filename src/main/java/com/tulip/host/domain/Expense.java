package com.tulip.host.domain;

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
@Table(name = "expense")
public class Expense extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "vendorName", length = 50)
    private String vendorName;

    @Size(max = 500)
    @NotNull
    @Column(name = "description", nullable = false, length = 500)
    private String description;

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
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction order;
}
