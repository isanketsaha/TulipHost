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
import lombok.Data;

@Entity
@Table(name = "s3_upload")
@Data
public class UploadRecord extends AbstractAuditingEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid", nullable = false, length = 100)
    private String uid;

    @Column(name = "file_name", length = 150)
    private String name;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "file_type", length = 50)
    private String type;

    @Column(name = "file_size")
    private Integer size;

    @ManyToOne(fetch = FetchType.LAZY, cascade =  {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "system_document_id",nullable = false)
    private SystemDocument systemDocument;

}
