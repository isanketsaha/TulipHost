package com.tulip.host.domain;

import com.tulip.host.enums.AcademicUploadType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "classroom_academic_upload")
public class ClassroomAcademicUpload extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_detail_id", nullable = false)
    private ClassDetail classDetail;

    @Column(name = "subject_key", nullable = false, length = 50)
    private String subjectKey;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_type", nullable = false, length = 20)
    private AcademicUploadType uploadType;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "s3_upload_id")
    private UploadRecord s3Upload;
}
