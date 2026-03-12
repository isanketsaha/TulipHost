package com.tulip.host.domain;

import com.tulip.host.enums.ExamType;
import com.tulip.host.enums.TermMarkType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "class_subject")
public class ClassSubject extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_detail_id", nullable = false)
    private ClassDetail classDetail;

    @NotNull
    @Column(name = "subject_key", nullable = false, length = 50)
    private String subjectKey;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @NotNull
    @Builder.Default
    @Column(name = "included", nullable = false)
    private Boolean included = true;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "class_subject_exam", joinColumns = @JoinColumn(name = "class_subject_id"))
    @Column(name = "exam_type", length = 20)
    @Enumerated(EnumType.STRING)
    private Set<ExamType> examTypes = new HashSet<>();

    /** Applies only when TERM is in examTypes. FULL = 80 ext + 10 internal; HALF = 50 ext only. */
    @Column(name = "term_mark_type", length = 10)
    @Enumerated(EnumType.STRING)
    private TermMarkType termMarkType;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "class_subject_assessment_param",
        joinColumns = @JoinColumn(name = "class_subject_id"),
        inverseJoinColumns = @JoinColumn(name = "assessment_parameter_id")
    )
    private Set<AssessmentParameter> assessmentParams = new HashSet<>();
}
