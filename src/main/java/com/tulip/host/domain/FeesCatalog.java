package com.tulip.host.domain;

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
@Table(name = "fees_catalog")
public class FeesCatalog extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "fees_name", nullable = false)
    private String feesName;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 20)
    @NotNull
    @Column(name = "applicable_rule", nullable = false, length = 20)
    private String applicableRule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "std_id")
    private ClassDetail std;
}
