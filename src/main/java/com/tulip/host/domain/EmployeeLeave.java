package com.tulip.host.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import com.tulip.host.enums.LeaveStatus;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_leave")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLeave extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_days", nullable = false, precision = 5, scale = 1)
    private BigDecimal totalDays;

    @Lob
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private LeaveStatus status = LeaveStatus.PENDING;


    private String approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Lob
    private String comments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employeeLeave", cascade = { CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REMOVE })
    private Set<Upload> documents;

    public void addUpload(Set<Upload> upload) {
        if (documents == null) {
            Set<Upload> uploadSet = new LinkedHashSet<>();
            uploadSet.addAll(upload);
            this.setDocuments(uploadSet);
        } else {
            this.documents.addAll(upload);
        }
        upload.forEach(item -> item.setEmployeeLeave(this));
    }

    public void mapUploads(){
        this.getDocuments().forEach(item -> item.setEmployeeLeave(this));
    }

}
