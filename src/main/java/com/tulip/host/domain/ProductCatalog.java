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
@Table(name = "product_catalog")
public class ProductCatalog extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 10)
    @NotNull
    @Column(name = "tag", nullable = false, length = 10)
    private String tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "std_id")
    private ClassDetail std;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active = false;

    @Size(max = 20)
    @Column(name = "size", length = 20)
    private String size;

    @Size(max = 20)
    @Column(name = "type", length = 20)
    private String category;
}
