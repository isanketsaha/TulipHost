package com.tulip.host.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemDocument extends AbstractAuditingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String uid;

    @Column(name = "file_name", length = 150)
    private String fileName;

    @Column(length = 50)
    private String status;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_size", columnDefinition = "INT DEFAULT 0")
    private Integer fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_category", nullable = false, length = 50)
    private DocumentCategory documentCategory;

    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassDetail classDetails;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;

    public enum DocumentCategory {
        TEMPLATE, ACADEMIC_CALENDAR, HOLIDAY_LIST, BANNER
    }
}
