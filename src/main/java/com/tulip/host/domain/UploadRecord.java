package com.tulip.host.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "s3_upload")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadRecord extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid", nullable = false, length = 100)
    private String uid;

    @Column(name = "file_name", length = 150)
    private String name;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "file_type", length = 255)
    private String type;

    @Column(name = "file_size")
    private Integer size;

    @Column(name = "upload_type", length = 30)
    private String uploadType;

    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "system_document_id", nullable = true)
    private SystemDocument systemDocument;
}
