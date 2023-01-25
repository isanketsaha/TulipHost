package com.tulip.host.domain;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    @Builder.Default
    private Boolean active = true;

    @Size(max = 20)
    @Column(name = "size", length = 20)
    private String size;

    @Size(max = 20)
    @Column(name = "type", length = 20)
    private String category;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<PurchaseLineItem> purchaseLineItems;
}
