package com.tulip.host.domain;

import com.tulip.host.enums.FeesRuleType;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @NotNull
    @Column(name = "applicable_rule", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private FeesRuleType applicableRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @Filter(name = "filterClass")
    @JoinColumn(name = "std_id")
    private ClassDetail std;
}
