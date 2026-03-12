package com.tulip.host.domain;

import com.tulip.host.enums.ExamType;
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
import jakarta.validation.constraints.NotNull;
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
@Table(name = "exam_marks_upload")
public class ExamMarksUpload extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_detail_id", nullable = false)
    private ClassDetail classDetail;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false, length = 10)
    private ExamType examType;

    @NotNull
    @Column(name = "exam_name", nullable = false, length = 50)
    private String examName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s3_upload_id")
    private UploadRecord s3Upload;
}
