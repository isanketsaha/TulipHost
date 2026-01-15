package com.tulip.host.domain;

import java.util.Set;

import org.hibernate.annotations.FilterDef;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "product_catalog")
@FilterDef(name = "filterCatalogNEPlaceholder", defaultCondition = "category != 'PLACEHOLDER'")
public class ProductCatalog extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Size(max = 100)
    @Column(name = "tag", nullable = false, length = 100)
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
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
    @Column(name = "category", length = 20)
    private String category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<PurchaseLineItem> purchaseLineItems;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<Inventory> inventories;
}
