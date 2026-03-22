package com.tulip.host.domain;

import com.tulip.host.enums.InventoryRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@ToString(exclude = { "employee", "product" })
@Entity
@Table(name = "inventory_request")
public class InventoryRequest extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_catalog_id", nullable = false)
    private ProductCatalog product;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Lob
    @Column(name = "justification")
    private String justification;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private InventoryRequestStatus status = InventoryRequestStatus.PENDING;

    // Stores the state machine instance ID for lookup during approval/reject/fulfill actions
    @Column(name = "machine_id", length = 120)
    private String machineId;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Override
    public Long getId() {
        return id;
    }
}
